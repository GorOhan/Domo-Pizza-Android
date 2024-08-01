package kk.domoRolls.ru.presentation.myaddresses

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.model.address.Coordinate
import kk.domoRolls.ru.domain.model.address.UserFirebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyAddressViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
) : ViewModel() {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()


    private val _myAddresses: MutableStateFlow<List<Address>> = MutableStateFlow(emptyList())
    val myAddresses = _myAddresses.asStateFlow()

    private val _event: MutableStateFlow<MyAddressesEvent> =
        MutableStateFlow(MyAddressesEvent.Nothing)
    val event = _event.asStateFlow()

    init {
        val newAddress = Address(
            building = "123B",
            city = "Saratov",
            comment = "",
            coordinate = Coordinate(lat = 51.56898499960524, lng = 46.00887),
            entrance = "",
            flat = "",
            floor = "",
            id = "NEW_ADDRESS_ID",
            intercom = "",
            isDefault = false,
            isPrivateHouse = false,
            minDeliveryPrice = 800,
            street = "Krasnnaya",
            streetId = "new-street-id",
            type = "Самовывоз"
        )

//        addAddress(dataStoreService.getUserData().id, newAddress,
//            onSuccess = {
//                println("Address added successfully!")
//            },
//            onFailure = { exception ->
//                println("Error: ${exception.message}")
//            }
//        )


        fetchUserAddresses(dataStoreService.getUserData().id,
            onSuccess = { addresses ->
                // Handle the list of addresses here
                _myAddresses.value = addresses
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

    fun addAddress(
        userId: String,
        newAddress: Address,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(userId)

        userRef.child("addresses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    // Get the current list of addresses
                    val currentAddresses =
                        snapshot.children.mapNotNull { it.getValue(Address::class.java) }
                            .toMutableList()

                    // Add the new address
                    currentAddresses.add(newAddress)

                    // Update the addresses list in the database
                    userRef.child("addresses").setValue(currentAddresses)
                        .addOnSuccessListener {
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

    fun setEvent(event: MyAddressesEvent){
        _event.value = event
    }

}