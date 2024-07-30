package kk.domoRolls.ru.presentation.cart


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.parseToGiftProduct
import kk.domoRolls.ru.util.parseToPromoCodes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val serviceRepository: ServiceRepository,
    private val firebaseRemoteConfig: FirebaseRemoteConfig
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


    init {
        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { token ->
                    serviceRepository.getMenuById(
                        getMenuRequest = GetMenuRequest(),
                        token = token.token
                    )
                }
                .collect { menuItems ->
                    _menu.value = menuItems
                    _currentCart.value = menuItems.filter { it.countInCart > 0 }
                    _showLoading.value = false
                    _spices.value =
                        menuItems.filter { it.categoryId == _categories.value.last().id }
                }
        }

        viewModelScope.launch {
            firebaseRemoteConfig.fetch(1)
                .addOnCompleteListener { taskFetch ->
                    if (taskFetch.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                val promoCodes: String =
                                    firebaseRemoteConfig.getString("promocodes")

                                promoCodes.parseToPromoCodes()?.let {
                                    _promoCodes.value = it
                                }

                                val giftProductJson: String =
                                    firebaseRemoteConfig.getString("gift_roll")
                                val gift = giftProductJson.parseToGiftProduct() ?: GiftProduct()
                                _gift.value = gift
                                if (gift.isAvailable) {
                                    _giftProduct.value =
                                        _menu.value.find { it.itemId == gift.id } ?: MenuItem()
                                }
                            }
                        }
                    }
                }
        }

        viewModelScope.launch {
            serviceRepository.getCategories().onEach {
                _categories.value = it
            }.collect()
        }
    }

    fun addToCart(menuItem: MenuItem) {
        serviceRepository.addToCart(menuItem)
    }

    fun removeFromCart(menuItem: MenuItem) {
        serviceRepository.removeFromCart(menuItem)
    }

    fun inputPromo(code: String) {
        if (code.length > 8) return
        _inputPromo.value = code
        _isPromoSuccess.value = null
    }

    fun confirmPromo() {
        _isPromoSuccess.value = promoCodes.value.any { it.value == _inputPromo.value }
    }


}