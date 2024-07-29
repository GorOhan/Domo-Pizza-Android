package kk.domoRolls.ru.presentation.cart


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.parseToGiftProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapConcat
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

    private val _menu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())

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
                }
        }

        viewModelScope.launch {
            firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener { taskFetch ->
                    if (taskFetch.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                val giftProductJson: String =
                                    firebaseRemoteConfig.getString("gift_roll")
                                val gift = giftProductJson.parseToGiftProduct() ?: GiftProduct()
                                _giftProduct.value =
                                    _menu.value.find { it.itemId == gift.id } ?: MenuItem()
                            }
                        }
                    }
                }
        }
    }

}