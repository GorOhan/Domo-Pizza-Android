package kk.domoRolls.ru.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.isWorkingTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    firebaseConfigRepository: FirebaseConfigRepository
) : ViewModel() {

    private val _currentCart: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    val currentCart = _currentCart.asStateFlow()

    private val _showLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showLoading = _showLoading.asStateFlow()

    private val _giftProduct: MutableStateFlow<MenuItem> = MutableStateFlow(MenuItem())
    val giftProduct = _giftProduct.asStateFlow()

    private val _spices: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    val spices = _spices.asStateFlow()

    private val _menu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())

    private val _categories: MutableStateFlow<List<ItemCategory>> = MutableStateFlow(emptyList())

    private val _gift: MutableStateFlow<GiftProduct> = MutableStateFlow(GiftProduct())
    val gift = _gift.asStateFlow()

    private val _inputPromo: MutableStateFlow<String> = MutableStateFlow("")
    val inputPromo = _inputPromo.asStateFlow()

    private val _isPromoSuccess: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isPromoSuccess = _isPromoSuccess.asStateFlow()

    private val _promoCodes: MutableStateFlow<List<PromoCode>> = MutableStateFlow(emptyList())
    private val promoCodes = _promoCodes.asStateFlow()

    private val _isWorkingTime: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isWorkingTime = _isWorkingTime.asStateFlow()

    private val _onEvent: MutableStateFlow<Event> = MutableStateFlow(Event.Nothing)
    val onEvent = _onEvent.asStateFlow()

    init {
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
                    _currentCart.value = menuItems.filter { it.countInCart > 0 }
                    _showLoading.value = false
                    _spices.value =
                        menuItems.filter { it.categoryId == _categories.value.last().id }
                    if (menuItems.none { it.countInCart > 0 }) _onEvent.value = Event.BackClick
                }
        }

        serviceRepository.getCategories()
            .onEach {
                _categories.value = it
            }.launchIn(viewModelScope)


        firebaseConfigRepository.getPromoCodes()
            .onEach { _promoCodes.value = it }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            combine(firebaseConfigRepository.getGiftProduct(), _menu) { gift, menu ->
                _gift.value = gift
                if (gift.isAvailable) {
                    _giftProduct.value =
                        _menu.value.find { item -> item.itemId == gift.id } ?: MenuItem()
                }
            }.collect()
        }

        firebaseConfigRepository.getWorkingHours()
            .onEach {
                _isWorkingTime.value = isWorkingTime(it)
            }
            .launchIn(viewModelScope)

    }

    private fun addToCart(menuItem: MenuItem) {
        serviceRepository.addToCart(menuItem)
    }

    private fun removeFromCart(menuItem: MenuItem) {
        serviceRepository.removeFromCart(menuItem)
    }

    private fun inputPromo(code: String) {
        if (code.length > 8) return
        _inputPromo.value = code
        _isPromoSuccess.value = null
    }

    private fun confirmPromo() {
        _isPromoSuccess.value = promoCodes.value.any { it.value == _inputPromo.value }
    }

    fun setEvent(event: Event) {
        _onEvent.value = event

        when (event) {
            Event.ConfirmPromo -> { confirmPromo() }
            is Event.AddToCart -> {
                addToCart(event.item)
            }

            is Event.RemoveFromCart -> {
                removeFromCart(event.item)
            }

            is Event.InputPromo -> {
                inputPromo(event.input)
            }

            Event.BackClick,
            Event.ConfirmOrder,
            Event.Nothing,
            Event.LogOut,
            is Event.NavigateClick -> {}
        }
    }
}