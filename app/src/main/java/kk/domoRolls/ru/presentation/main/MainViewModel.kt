package kk.domoRolls.ru.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
) : ViewModel() {

    private val _user: MutableStateFlow<User?> = MutableStateFlow(User())
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            _user.value = dataStoreService.getUserData()
        }
    }
}