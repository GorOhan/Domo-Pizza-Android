package kk.domoRolls.ru.presentation.myAddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.map.Polygon
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressMapViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val firebaseConfigRepository: FirebaseConfigRepository,
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _mapData: MutableStateFlow<List<Polygon>> = MutableStateFlow(emptyList())
    val mapData = _mapData.asStateFlow()

    private val _inOrderMode: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val inOrderMode = _inOrderMode.asStateFlow()

    private val _currentAddress: MutableStateFlow<String> = MutableStateFlow("")
    val currentAddress = _currentAddress.asStateFlow()


    init {
        viewModelScope.launch {
            firebaseConfigRepository.getPolygons()
                .onEach {
                    _mapData.value = it
                }
                .collect()
        }
    }

    fun setOrderMode(inOrderMode: Boolean) {
        _inOrderMode.value = inOrderMode
    }

    fun setCurrentAddress(inputAddress: String) {
        _currentAddress.value = inputAddress
    }
}