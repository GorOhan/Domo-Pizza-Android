package kk.domoRolls.ru.presentation.payorder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.model.map.Polygon
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tinkoff.acquiring.sdk.AcquiringSdk
import ru.tinkoff.acquiring.sdk.models.enums.CheckType
import ru.tinkoff.acquiring.sdk.requests.GetStateRequest
import ru.tinkoff.acquiring.sdk.responses.FinishAuthorizeResponse
import ru.tinkoff.acquiring.sdk.utils.Money
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class PayOrderViewModel @Inject constructor(
    serviceRepository: ServiceRepository,
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

    init {
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


     fun checkPaymentStatus(sdk: AcquiringSdk,id: Int) {


        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                sdk.init {
//                    orderId = UUID.randomUUID().toString()
//                    amount = 100
//                    customerKey = UUID.randomUUID()
//                        .toString()
//
//                }.execute(
//
//                )
//            }

            withContext(Dispatchers.IO) {
                repeat(40) {
                    sdk.getState {
                        this.paymentId = id.toLong()


                    }
                        .execute(
                            onSuccess = { response ->
                                Log.i("CHECK_STATUS", response.toString())
                            },
                            onFailure = {
                                Log.i("CHECK_STATUS","payment id $id")

                            },
                        )
                    delay(2500L)
                }
            }
        }

    }
}