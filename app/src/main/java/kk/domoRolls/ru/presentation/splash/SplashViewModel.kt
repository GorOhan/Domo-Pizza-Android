package kk.domoRolls.ru.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
) : ViewModel() {

    private val _userId: MutableStateFlow<String?> = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    init {
        viewModelScope.launch {
            _userId.value = dataStoreService.getUserData().id
        }
    }
}