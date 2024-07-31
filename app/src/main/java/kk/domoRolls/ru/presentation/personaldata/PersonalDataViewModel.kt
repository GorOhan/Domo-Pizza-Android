package kk.domoRolls.ru.presentation.personaldata

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PersonalDataViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
) : ViewModel() {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _inputUserName: MutableStateFlow<String> =
        MutableStateFlow(dataStoreService.getUserData().name)
    val inputUserName = _inputUserName.asStateFlow()

    private val _inputUserPhone: MutableStateFlow<String> =
        MutableStateFlow(dataStoreService.getUserData().phone)
    val inputUserPhone = _inputUserPhone.asStateFlow()

    private val _inputUserEmail: MutableStateFlow<String> = MutableStateFlow(dataStoreService.getUserData().email)
    val inputUserEmail = _inputUserEmail.asStateFlow()

    private val _onEvent: MutableStateFlow<PersonalDataEvent> = MutableStateFlow(PersonalDataEvent.Nothing)
    val onEvent = _onEvent.asStateFlow()

    init {
        observeDB()

    }

    fun saveChanges() {
        // Reference to the user's data

        val userId = dataStoreService.getUserData().id
        // Update the username
        val updates = hashMapOf<String, Any>(
            "username" to _inputUserName.value,
            "phone" to _inputUserPhone.value,
            "email" to _inputUserEmail.value
        )

        databaseReference.child("users").child(userId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Username updated successfully
                    Log.d("Firebase", "Username updated.")
                    observeDB()
                } else {
                    // Handle the error
                    Log.e("Firebase", "Failed to update username.", task.exception)
                }
            }
    }

    fun setEvent(event: PersonalDataEvent) {
        _onEvent.value = event
        when (event) {
            PersonalDataEvent.BackClick -> {}
            is PersonalDataEvent.InputEmail -> {
                _inputUserEmail.value = event.input
            }

            is PersonalDataEvent.InputName -> {
                _inputUserName.value = event.input
            }

            is PersonalDataEvent.InputPhone -> {
                if (event.input.length < 11) _inputUserPhone.value = event.input
            }

            PersonalDataEvent.ConfirmChanges -> {
                saveChanges()
            }

            PersonalDataEvent.DeleteAccount -> {}
            PersonalDataEvent.Nothing -> {}
        }
    }

    fun observeDB() {
        databaseReference.child("users").child(dataStoreService.getUserData().id)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    val mapData = value as HashMap<String, String>

                    _inputUserName.value = mapData["username"] ?: ""
                    _inputUserPhone.value = mapData["phone"] ?: ""
                    _inputUserEmail.value = mapData["email"] ?: ""

                    dataStoreService.setUserData(
                        User(
                            id = dataStoreService.getUserData().id,
                            name = _inputUserName.value,
                            phone = inputUserPhone.value,
                            email = _inputUserEmail.value
                        )
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}