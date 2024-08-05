package kk.domoRolls.ru.presentation.myaddresses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAddressViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val firebaseConfigRepository: FirebaseConfigRepository,
) : ViewModel() {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _myAddresses: MutableStateFlow<List<Address>> = MutableStateFlow(emptyList())
    val myAddresses = _myAddresses.asStateFlow()

    private val _event: MutableSharedFlow<MyAddressesEvent> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    init {
        firebaseConfigRepository.fetchAddresses()
        firebaseConfigRepository
            .getAddresses()
            .onEach { _myAddresses.value = it }
            .launchIn(viewModelScope)
    }

    fun setEvent(event: MyAddressesEvent){
        viewModelScope.launch {
            _event.emit(event)
        }
    }

}