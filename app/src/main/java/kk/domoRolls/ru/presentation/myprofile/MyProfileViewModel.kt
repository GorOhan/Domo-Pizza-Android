package kk.domoRolls.ru.presentation.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.util.parseToPromoCodes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _promoCodes: MutableStateFlow<List<PromoCode>> = MutableStateFlow(emptyList())
    val promoCodes = _promoCodes.asStateFlow()

    init {
        firebaseRemoteConfig.fetch(10)
            .addOnCompleteListener { taskFetch ->
                if (taskFetch.isSuccessful) {
                    firebaseRemoteConfig.activate().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val promoCodes: String = firebaseRemoteConfig.getString("promocodes")

                            promoCodes.parseToPromoCodes()?.let {
                                _promoCodes.value = it
                            }
                        }
                    }
                }
            }
    }


 fun logOut(){
     viewModelScope.launch {
         dataStoreService.setUserData(User())
     }
 }
}