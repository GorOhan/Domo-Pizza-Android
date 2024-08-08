package kk.domoRolls.ru.presentation.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetOrdersRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val firebaseConfigRepository: FirebaseConfigRepository,
    private val serviceRepository: ServiceRepository,
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _promoCodes: MutableStateFlow<List<PromoCode>> = MutableStateFlow(emptyList())
    val promoCodes = _promoCodes.asStateFlow()

    private val _myAddressesCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val myAddressesCount = _myAddressesCount.asStateFlow()

    private val _myOrdersCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val myOrdersCount = _myOrdersCount.asStateFlow()

    init {
        getOrders()
        viewModelScope.launch {
            firebaseConfigRepository.getPromoCodes()
                .onEach { _promoCodes.value = it }
                .collect()
        }

        firebaseConfigRepository.fetchAddresses()

        firebaseConfigRepository.getAddresses()
            .onEach { _myAddressesCount.value = it.size  }
            .launchIn(viewModelScope)

    }


    fun logOut() {
        viewModelScope.launch {
            dataStoreService.setUserData(User())
        }
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
                        _myOrdersCount.value = it.size
                    }
                }
                .catch {

                }
                .collect()
        }
    }
}