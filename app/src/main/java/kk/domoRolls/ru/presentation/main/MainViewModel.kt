package kk.domoRolls.ru.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetOrdersRequest
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.isWorkingTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val serviceRepository: ServiceRepository,
    private val firebaseConfigRepository: FirebaseConfigRepository,
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _promoStoryList: MutableStateFlow<List<PromoStory>> = MutableStateFlow(emptyList())
    val promoList = _promoStoryList.asStateFlow()

    private val _menu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    val menu = _menu.asStateFlow()

    private val _showLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showLoading = _showLoading.asStateFlow()

    private val _categories: MutableStateFlow<List<ItemCategory>> = MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    private val _isOpen: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isOpen = _isOpen.asStateFlow()

    private val _defaultAddress: MutableStateFlow<Address> = MutableStateFlow(Address(privateHouse = false))
    val defaultAddress = _defaultAddress.asStateFlow()

    init {

        viewModelScope.launch {
            firebaseConfigRepository.fetchAddresses()
            _showLoading.value = true
        }

        viewModelScope.launch {
            firebaseConfigRepository.getPromoStories()
                .onEach { _promoStoryList.value = it }
                .collect()

            firebaseConfigRepository.getWorkingHours()
                .onEach {
                    _isOpen.value = isWorkingTime(it)
                }
                .collect()
        }



        viewModelScope.launch {
            serviceRepository.getCategories().onEach {
                _categories.value = it
                categoryCheckFirst()
            }.collect()
        }

        viewModelScope.launch {
            fetchMenu()
        }

        firebaseConfigRepository.getAddresses()
            .onEach {
                if (it.isNotEmpty()) {
                    _defaultAddress.value = it.find { it.default } ?: it.first()
                }
            }
            .launchIn(viewModelScope)
        getOrders()

    }

    fun addToCart(menuItem: MenuItem) {
        serviceRepository.addToCart(menuItem)
    }

    fun removeFromCart(menuItem: MenuItem) {
        serviceRepository.removeFromCart(menuItem)
    }


    fun categoryCheck(menuItem: ItemCategory) {
        val item = categories.value.indexOf(menuItem)
        val lists = categories.value.toMutableList()
        lists.forEach {
            it.isChecked = false
        }
        lists[item] = categories.value[item].copy(isChecked = true)
        _categories.value = lists
    }

    private fun categoryCheckFirst() {
        val item = 0
        val lists = categories.value.toMutableList()
        lists.forEach {
            it.isChecked = false
        }
        if (categories.value.isNotEmpty()) {
            lists[item] = categories.value[item].copy(isChecked = true)
        }

        _categories.value = lists
    }

    fun hideSleepView() {
        _isOpen.value = true
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
                .catch {  }
                .collect { menuItems ->
                    _menu.value = menuItems
                    _showLoading.value = false
                }
        }
    }

    private fun getOrders() {
        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { token ->
                    serviceRepository.getOrders(
                        getOrdersRequest = GetOrdersRequest(
                            phone = "+7${_user.value.phone}"
                        ),
                        token = token.token
                    )
                }
                .catch { }
                .collect()
        }
    }


}