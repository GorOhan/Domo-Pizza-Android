package kk.domoRolls.ru.presentation.orderstatus

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetOrderByIdRequest
import kk.domoRolls.ru.data.model.order.Order
import kk.domoRolls.ru.data.model.order.OrderDeliveryTime
import kk.domoRolls.ru.data.model.order.OrderStatus
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.presentation.myaddresses.MyAddressesEvent
import kk.domoRolls.ru.util.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderStatusViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val serviceRepository: ServiceRepository,
    private val firebaseConfigRepository: FirebaseConfigRepository,
) : BaseViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _event: MutableSharedFlow<MyAddressesEvent> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _order: MutableStateFlow<Order> = MutableStateFlow(Order())
    val order = _order.asStateFlow()

    private val _deliveryTime: MutableStateFlow<String> = MutableStateFlow("")
    val deliveryTime = _deliveryTime.asStateFlow()


    init {
        _user.value = dataStoreService.getUserData()

    }

    fun getOrderById(orderId: String) {
        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { tokenResponse ->

                    serviceRepository.getOrderById(
                        GetOrderByIdRequest(listOf(orderId)),
                        token = tokenResponse.token
                    )
                }
                .onEach { orderResponse ->
                    orderResponse?.let {
                        _order.value = it
                        _deliveryTime.value = OrderStatus.entries.find { it.value == orderResponse.orderItem?.status }?.OrderDeliveryTime(
                            firebaseConfigRepository.getDeliveryTime().first().toInt())?:""
                    }
                }
                .catch {
                    _showMainError.value = true
                }
                .collect()
        }
    }

}