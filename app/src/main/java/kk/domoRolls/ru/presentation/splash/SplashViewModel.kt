package kk.domoRolls.ru.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.GetStreetsRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val serviceRepository: ServiceRepository,
) : ViewModel() {

    private val _userId: MutableStateFlow<String?> = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    init {
        viewModelScope.launch {
            _userId.value = dataStoreService.getUserData().id
//            serviceRepository.getToken(ServiceTokenRequest())
//                .flatMapConcat { token ->
//                    serviceRepository.getMenuById(GetMenuRequest(), token.token)
//                        .flatMapConcat { menu ->
//                            serviceRepository.getStopLists(GetStopListRequest(), token.token)
//                        }
//                }
//                .collect { stopLists ->
//
//                    // Handle the collected stopLists here
//                }

            serviceRepository.getToken(ServiceTokenRequest())
                .onEach {
                    serviceRepository.getStreets(GetStreetsRequest(), it.token)
                        .onEach {
                            println("CITY  ${it.correlationId}")
                        }
                        .collect()
                }.collect()


        }
    }
}