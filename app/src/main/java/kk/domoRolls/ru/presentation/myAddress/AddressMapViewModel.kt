package kk.domoRolls.ru.presentation.myAddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.model.map.Polygon
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
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

    private val _currentAddressModel: MutableStateFlow<Address> = MutableStateFlow(Address())
    val currentAddressModel = _currentAddressModel.asStateFlow()

    private val _defaultAddress: MutableStateFlow<Address?> = MutableStateFlow(null)
    val defaultAddress = _defaultAddress.asStateFlow()

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

    fun setCurrentAddressModel(inputAddress: Address) {
        _currentAddressModel.value = inputAddress
    }

    fun addAddress(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(dataStoreService.getUserData().id)

        userRef.child("addresses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    // Get the current list of addresses
                    val currentAddresses = snapshot.children.mapNotNull { it.getValue(Address::class.java) }.toMutableList()

                         defaultAddress.value?.let {default ->
                             currentAddresses.removeIf { it.id == default.id }
                             currentAddresses.add(currentAddressModel.value)
                         }?:run {
                             currentAddresses.add(
                                 currentAddressModel.value.copy(
                                     id = UUID.randomUUID().toString()
                                 )
                             )
                         }


                    userRef.child("addresses").setValue(currentAddresses)
                        .addOnSuccessListener {
                            firebaseConfigRepository.fetchAddresses()
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                } catch (e: Exception) {
                    onFailure(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    fun setCurrentAddressId(addressId: String) {
        _defaultAddress.value = firebaseConfigRepository.getAddressById(addressId)
        _defaultAddress.value?.let {
            _currentAddressModel.value = it
        }
    }
}