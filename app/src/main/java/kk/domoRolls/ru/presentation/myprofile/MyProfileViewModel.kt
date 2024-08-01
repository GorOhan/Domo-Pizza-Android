package kk.domoRolls.ru.presentation.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.model.address.UserFirebase
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

    private val _myAddressesCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val myAddressesCount = _myAddressesCount.asStateFlow()

    init {
        viewModelScope.launch {
            firebaseConfigRepository.getPromoCodes()
                .onEach { _promoCodes.value = it }
                .collect()
        }

        fetchUserAddresses(dataStoreService.getUserData().id,
            onSuccess = { addresses ->
                // Handle the list of addresses here
                _myAddressesCount.value = addresses.size
                for (address in addresses) {
                    println("Address: ${address.street}, ${address.city}")
                }
            },
            onFailure = { exception ->
                // Handle the error here
                println("Error: ${exception.message}")
            }
        )
    }


    fun logOut() {
        viewModelScope.launch {
            dataStoreService.setUserData(User())
        }
    }

    private fun fetchUserAddresses(
        userId: String,
        onSuccess: (List<Address>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val user = snapshot.getValue(UserFirebase::class.java)
                    val addresses = user?.addresses ?: emptyList()
                    onSuccess(addresses)
                } catch (e: Exception) {
                    onFailure(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }
}