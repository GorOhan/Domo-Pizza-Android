package kk.domoRolls.ru.presentation.myaddresses

import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.util.BaseViewModel
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
) : BaseViewModel() {

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

        if (event is MyAddressesEvent.UpdateAddress){
            makeDefaultAddress(
                address = event.address,
                onSuccess = {
                    firebaseConfigRepository.fetchAddresses()
                },
                onFailure = {}
                )
        }
    }

    fun makeDefaultAddress(
        address: Address,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(dataStoreService.getUserData().id)

        userRef.child("addresses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    // Get the current list of addresses
                    val currentAddresses = snapshot.children.mapNotNull { it.getValue(Address::class.java) }
                        .toMutableList()

                    val newAddresses = currentAddresses.map { it.copy(default = false) }.toMutableList()
                    val index = newAddresses.indexOfFirst { it.id == address.id }
                    newAddresses[index] = address

                    userRef.child("addresses").setValue(newAddresses)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                } catch (e: Exception) {
                    _showMainError.value = true
                    onFailure(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

}