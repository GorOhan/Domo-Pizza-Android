package kk.domoRolls.ru.presentation.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val firebaseConfigRepository: FirebaseConfigRepository,
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _promoCodes: MutableStateFlow<List<PromoCode>> = MutableStateFlow(emptyList())
    val promoCodes = _promoCodes.asStateFlow()

    init {
        viewModelScope.launch {
            firebaseConfigRepository.getPromoCodes()
                .onEach { _promoCodes.value = it }
                .collect()
        }
    }


    fun logOut() {
        viewModelScope.launch {
            dataStoreService.setUserData(User())
        }
    }
}