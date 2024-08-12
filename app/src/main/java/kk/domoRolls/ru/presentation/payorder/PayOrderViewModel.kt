package kk.domoRolls.ru.presentation.payorder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetOrderByIdRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.model.sendorder.SendAddress
import kk.domoRolls.ru.data.model.sendorder.SendCustomer
import kk.domoRolls.ru.data.model.sendorder.SendDeliveryPoint
import kk.domoRolls.ru.data.model.sendorder.SendGuests
import kk.domoRolls.ru.data.model.sendorder.SendItem
import kk.domoRolls.ru.data.model.sendorder.SendOrder
import kk.domoRolls.ru.data.model.sendorder.SendOrderData
import kk.domoRolls.ru.data.model.sendorder.SendStreet
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.sliceJsonFromResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
) : ViewModel() {

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
    var activeOrderStatus = ""


    init {
        paymentResponseBody.onEach {
            if (it.contains("Got server response:")) {

                try {
                    Log.i("TINKOFF BODY", it )
                    val new = it.sliceJsonFromResponse()
                    val isSuccess = new?.let { it1 -> JSONObject(it1).getBoolean("Success") }
                    val errorCode = new?.let { it1 -> JSONObject(it1).getInt("ErrorCode") }
                    val status =
                        new?.let { it1 -> JSONObject(it1).getBoolean("Status") } ?: "NOT STATUS"



                    Log.i("TINKOFF BODY", new ?: "ERROR")
                    Log.i("TINKOFF BODY", isSuccess.toString())
                    Log.i("TINKOFF BODY", errorCode.toString())
                    if (status == "CONFIRMED") {
                        _paymentAlreadyConfirmed.value = true
                    }

                } catch (e: Exception) {
                    Log.i("TINKOFF BODY", "ERROR")

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
        val promo = serviceRepository.getPromoCode()

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
        _paymentAlreadyConfirmed.value = true
    }

    fun sendOrderToIIKO() {
        val sendOrderData = SendOrderData(
            terminalGroupId = "dbd89055-96a1-4223-81ba-40afe53bbd04",
            organizationId = "03a1584e-1c80-4071-829d-997688b68cba",
            order = SendOrder(
                phone = "+7 937 885-29-05",
                orderTypeId = "76067ea3-356f-eb93-9d14-1fa00d082c4e",
                items = listOf(
                    SendItem(
                        type = "Product",
                        amount = 4,
                        productId = "ce4e3db9-0fdb-49e7-b49c-81339923c631",
                        price = 550,
                        productSizeId = ""
                    ),
                    SendItem(
                        type = "Product",
                        amount = 1,
                        productId = "ce4e3db9-0fdb-49e7-b49c-81339923c631",
                        price = 0,
                        productSizeId = ""
                    )
                ),
                customer = SendCustomer(
                    name = "ТЕСТ! НЕ ГОТОВИТЬ"
                ),
                deliveryPoint = SendDeliveryPoint(
                    comment = "проспект Кирова, дом 3",
                    address = SendAddress(
                        flat = "",
                        house = "3",
                        entrance = "",
                        floor = "",
                        street = SendStreet(
                            id = ""
                        ),
                        doorphone = ""
                    )
                ),
                guests = SendGuests(
                    count = 1
                )
            )
        )

        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { tokenResponse ->

                    serviceRepository.sendOrder(
                        sendOrderData,
                        token = tokenResponse.token
                    )
                }
                .onEach { paymentId ->
                    paymentId.orderInfo.id.let {
                        checkPaymentCreationStatus(it, Date())
                    }
                }
                .catch {
                    Log.d("SENDORDER", it.localizedMessage)
                }
                .collect()
        }
    }

    fun paymentDone() {
        //todo add orderid to orders firebase
    }

    private fun checkPaymentCreationStatus(paymentId: String, startDate: Date) {
        viewModelScope.launch {
            while (Date().time - startDate.time < 5000) {
                if (activeOrderStatus  == "Success") return@launch
                serviceRepository.getToken(ServiceTokenRequest())
                    .flatMapConcat { tokenResponse ->

                        serviceRepository.getOrderCreationStatus(
                            GetOrderByIdRequest(listOf(paymentId)),
                            token = tokenResponse.token
                        )
                    }
                    .onEach { it ->
                        Log.i("ActiveOrderStatus", it.toString())

                        if (it == "Success") {
                            activeOrderStatus = "Success"
                        }
                    }
                    .catch {
                    }
                    .collect()

                delay(300L)
            }
        }
    }
}