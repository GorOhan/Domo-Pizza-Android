package kk.domoRolls.ru.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.parseJson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _promoList: MutableStateFlow<List<Promo>> = MutableStateFlow(emptyList())
    val promoList = _promoList.asStateFlow()

    private val _menu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    val menu = _menu.asStateFlow()

    private val _showLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showLoading = _showLoading.asStateFlow()

    init {

        viewModelScope.launch {
            _showLoading.value = true

            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val otpMessage: String = firebaseRemoteConfig.getString("promo_list")

            _user.value = dataStoreService.getUserData()
            otpMessage.parseJson()?.let {
                _promoList.value = it
            }


//            firebaseRemoteConfig.fetch(100)
//                .addOnCompleteListener { taskFetch ->
//                    if (taskFetch.isSuccessful) {
//                        firebaseRemoteConfig.activate().addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                val otpMessage: String = firebaseRemoteConfig.getString("promo_list")
//
//                                _user.value = dataStoreService.getUserData()
//                                 otpMessage.parseJson()?.let {
//                                     _promoList.value = it
//                                 }
//                                println("PROMO PROM ${_promoList.value}")
//                            }
//                        }
//                    }
//                }
        }

        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { token ->
                    serviceRepository.getMenuById(
                        getMenuRequest = GetMenuRequest(),
                        token = token.token)
                }
                .collect { menuItems ->
                    _menu.value = menuItems
                    _showLoading.value = false
                }
        }
    }

    fun addToCart(menuItem: MenuItem){
        serviceRepository.addToCart(menuItem)
    }

    fun removeFromCart(menuItem: MenuItem){
        serviceRepository.removeFromCart(menuItem)
    }

}