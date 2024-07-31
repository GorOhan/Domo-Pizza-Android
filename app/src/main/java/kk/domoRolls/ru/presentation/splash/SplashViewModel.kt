package kk.domoRolls.ru.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    private val firebaseConfigRepository: FirebaseConfigRepository,
) : ViewModel() {

    private val _userId: MutableStateFlow<String?> = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    private val _isAppAvailable: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isAppAvailable = _isAppAvailable.asStateFlow()

    init {
        initApp()
    }

    fun initApp() {

        viewModelScope.launch {
            val userCall = async {
                _userId.value = dataStoreService.getUserData().id
                serviceRepository.getToken(ServiceTokenRequest())
                    .flatMapConcat { token ->
                        serviceRepository.getStopListsIds(
                            GetStopListRequest(), token.token
                        ).flatMapConcat { stopList ->
                            serviceRepository.getMenuById(
                                disableIds = stopList,
                                getMenuRequest = GetMenuRequest(),
                                token = token.token
                            )
                        }
                    }.catch {

                    }
                    .collect()
            }
            val firebase = async {
                firebaseConfigRepository.getAppAvailable().onEach {
                    _isAppAvailable.value = it
                }
                    .catch { }
                    .collect()
            }

            firebase.await()
            userCall.await()
        }
    }
}