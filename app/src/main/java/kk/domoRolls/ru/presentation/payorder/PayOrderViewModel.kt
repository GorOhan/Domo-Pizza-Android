package kk.domoRolls.ru.presentation.payorder

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetOrderByIdRequest
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.model.sendorder.createSendOrderData
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.BaseViewModel
import kk.domoRolls.ru.util.sliceJsonFromResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class PayOrderViewModel @Inject constructor(
    val serviceRepository: ServiceRepository,
    firebaseConfigRepository: FirebaseConfigRepository,
    val dataStoreService: DataStoreService,
) : BaseViewModel() {

    private val _cartCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val cartCount = _cartCount.asStateFlow()

    private val _cartPrice: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val cartPrice = _cartPrice.asStateFlow()

    private val _discount: MutableStateFlow<Int> = MutableStateFlow(0)
    val discount = _discount.asStateFlow()

    private val _deliveryTime: MutableStateFlow<String> = MutableStateFlow("45")
    val deliveryTime = _deliveryTime.asStateFlow()

    private val _defaultAddress: MutableStateFlow<Address> =
        MutableStateFlow(Address(privateHouse = false))
    val defaultAddress = _defaultAddress.asStateFlow()

    private val _paymentResponseBody: MutableStateFlow<String> = MutableStateFlow("")
    private val paymentResponseBody = _paymentResponseBody.asStateFlow()

    private val _paymentAlreadyConfirmed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val paymentAlreadyConfirmed = _paymentAlreadyConfirmed.asStateFlow()

    private val _comment: MutableStateFlow<String> = MutableStateFlow("")
    val comment = _comment.asStateFlow()

    private val _navigateToOrderStatus: MutableSharedFlow<String?> = MutableSharedFlow()
    val navigateToOrderStatus = _navigateToOrderStatus.asSharedFlow()

    private val _showMinPriceError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showMinPriceError = _showMinPriceError.asStateFlow()

    private var activeOrderStatus = ""

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user.asStateFlow()

    private var currentCart: List<MenuItem>? = null
    private var usedPromoCode: PromoCode? = null
    private var deviceCount: Int = 0

    private val _enableToPay: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val enableToPay = _enableToPay.asStateFlow()

    private val _pickedTime: MutableStateFlow<String> = MutableStateFlow("")
    val pickedTime = _pickedTime.asStateFlow()


    init {
        _user.value = dataStoreService.getUserData()

        serviceRepository.getDeviceCount()
            .onEach { deviceCount = it }
            .launchIn(viewModelScope)

        combine(cartPrice, defaultAddress) { cartPrice, defaultAddress ->
            _enableToPay.value = cartPrice != 0.0 &&
                    defaultAddress.minDeliveryPrice != 0 &&
                    cartPrice > defaultAddress.minDeliveryPrice
        }.launchIn(viewModelScope)

        paymentResponseBody.onEach {
            if (it.contains("Got server response:")) {

                try {
                    Log.i("TINKOFF BODY", it)
                    val new = it.sliceJsonFromResponse()
                    val isSuccess = new?.let { it1 -> JSONObject(it1).getBoolean("Success") }
                    val errorCode = new?.let { it1 -> JSONObject(it1).getInt("ErrorCode") }
                    val status =
                        new?.let { it1 -> JSONObject(it1).getString("Status") } ?: "NOT STATUS"



                    Log.i("TINKOFF BODY", new ?: "ERROR")
                    Log.i("TINKOFF BODY", isSuccess.toString())
                    Log.i("TINKOFF BODY", errorCode.toString())
                    if (status == "CONFIRMED") {
                        _paymentAlreadyConfirmed.value = true
                        sendOrderToIIKO()
                    }

                } catch (e: Exception) {
                    _showContactToSupport.value = true
                }

            }
        }.launchIn(viewModelScope)


        firebaseConfigRepository.getDeliveryTime()
            .onEach { _deliveryTime.value = it }
            .launchIn(viewModelScope)

        firebaseConfigRepository.getAddresses()
            .onEach {
                if (it.isNotEmpty()) {
                    _defaultAddress.value = it.find { it.default } ?: it.first()
                }
            }
            .launchIn(viewModelScope)

        val cart = serviceRepository.getCart()
        currentCart = cart

        val promo = serviceRepository.getUsedPromoCode()
        usedPromoCode = promo

        _cartPrice.value = cart.filter { menuItem -> menuItem.countInCart > 0 }
            .map { Pair(it.countInCart, it.itemSizes?.first()?.prices?.first()?.price ?: 0.0) }
            .sumOf { it.second * it.first }

        promo?.let {
            _discount.value = (cartPrice.value * it.discount).roundToInt()
            _cartPrice.value *= (1 - it.discount)
        }
        _cartCount.value = cart.size


    }

    fun onLog(body: String) {
        _paymentResponseBody.value = body

    }

    fun setPaymentAlreadyConfirmed() {
        if (_paymentAlreadyConfirmed.value.not()){
            sendOrderToIIKO()
            _paymentAlreadyConfirmed.value = true
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun sendOrderToIIKO() {

        val sendOrderData = createSendOrderData(
            user = _user.value ?: User(),
            currentCart = currentCart ?: emptyList(),
            defaultAddress = defaultAddress.value,
            additionalComment = _comment.value,
            usedPromoCode = usedPromoCode ?: PromoCode(),
            deviceCount = deviceCount,
            pickedTime = _pickedTime.value
        )
        if (cartPrice.value != 0.0 &&
            defaultAddress.value.minDeliveryPrice != 0 &&
            cartPrice.value > defaultAddress.value.minDeliveryPrice
        ) {
            viewModelScope.launch {
                serviceRepository.getToken(ServiceTokenRequest())
                    .flatMapConcat { tokenResponse ->
                        serviceRepository.sendOrder(
                            sendOrderData,
                            token = tokenResponse.token
                        )
                    }
                    .onEach { orderResponse ->
                        orderResponse.orderInfo.id.let {
                            addOrderIdInFirebase(orderId = it, userId = _user.value?.id ?: "", {})
                            checkPaymentCreationStatus(it, Date())

                            usedPromoCode?.let { code ->
                                addPromoCodeInFirebase(code.value, {}, {})
                            }
                        }
                    }
                    .catch {
                        _showContactToSupport.value = true
                    }
                    .collect()
            }
        } else {
            _showContactToSupport.value = true
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun checkPaymentCreationStatus(paymentId: String, startDate: Date) {
        viewModelScope.launch {
            while (Date().time - startDate.time < 5000) {
                if (activeOrderStatus == "Success") return@launch
                serviceRepository.getToken(ServiceTokenRequest())
                    .flatMapConcat { tokenResponse ->

                        serviceRepository.getOrderById(
                            GetOrderByIdRequest(listOf(paymentId)),
                            token = tokenResponse.token
                        )
                    }
                    .onEach { it ->
                        Log.i("ActiveOrderStatus", it.toString())

                        if (it?.creationStatus == "Success") {
                            serviceRepository.resetCart()
                            _navigateToOrderStatus.emit(it.id)
                            activeOrderStatus = "Success"
                        }
                    }
                    .catch {
                        _showContactToSupport.value = true
                    }
                    .collect()

                delay(300L)
            }
        }
    }

    fun inputComment(comment: String) {
        _comment.value = comment
    }

    fun setPickedTime(time: String) {
        _pickedTime.value = time
    }

    private fun addOrderIdInFirebase(
        orderId: String,
        userId: String,
        completion: () -> Unit
    ) {
        val ref = FirebaseDatabase.getInstance().reference

        ref.child("orders").child(orderId).setValue(userId)
        completion()
    }

    private fun addPromoCodeInFirebase(
        usedPromoCode: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(dataStoreService.getUserData().id)

        userRef.child("usedPromocodes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val promoCodes =
                        snapshot.children.mapNotNull { it.getValue(String::class.java) }
                            .toMutableList()

                    promoCodes.add(usedPromoCode)

                    userRef.child("usedPromocodes").setValue(promoCodes)
                        .addOnSuccessListener {
                            onSuccess()

                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                } catch (e: Exception) {
                    onFailure(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    fun showMinPriceError(show: Boolean){
        _showMinPriceError.value = show
    }
}