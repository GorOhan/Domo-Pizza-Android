package kk.domoRolls.ru.presentation.personaldata

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PersonalDataViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _promoCodes: MutableStateFlow<List<PromoCode>> = MutableStateFlow(emptyList())
    val promoCodes = _promoCodes.asStateFlow()


}