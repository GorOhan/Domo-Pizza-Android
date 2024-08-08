package kk.domoRolls.ru.presentation.myorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetOrdersRequest
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.Order
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.presentation.myaddresses.MyAddressesEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val firebaseConfigRepository: FirebaseConfigRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _myOrders: MutableStateFlow<List<Order>> = MutableStateFlow(emptyList())
    val myOrders = _myOrders.asStateFlow()

    private val _event: MutableSharedFlow<MyAddressesEvent> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _menu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    val menu = _menu.asStateFlow()

    init {
        fetchMenu()
        getOrders()

    }

    private fun getOrders() {
        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { token ->
                    serviceRepository.getOrders(
                        getOrdersRequest = GetOrdersRequest(
                          phone = "+79271266306"
                          //  phone = "+7${_user.value.phone}"
                        ),
                        token = token.token
                    )
                }.onEach { it ->
                    it?.ordersByOrganizations?.first()?.orders?.let {
                        _myOrders.value = it
                    }
                }
                .catch {

                }
                .collect()
        }
    }


    private fun fetchMenu() {
        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { token ->
                    serviceRepository.getMenuById(
                        getMenuRequest = GetMenuRequest(),
                        token = token.token
                    )
                }
                .catch { }
                .collect { menuItems ->
                    _menu.value = menuItems
                }
        }
    }

    fun getImagesUrls(productId: List<String?>): List<String> {
        val imagesLists = mutableListOf<String>()
        val itemsById = _menu.value.map { Pair(it.itemId, it.itemSizes?.first()?.buttonImageUrl) }
        productId.forEach { id ->
            itemsById.find { it.first == id }?.second?.let { imagesLists.add(it) }
        }
        return imagesLists
    }


}