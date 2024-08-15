package kk.domoRolls.ru.presentation.myprofile

import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetOrdersRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val firebaseConfigRepository: FirebaseConfigRepository,
    private val serviceRepository: ServiceRepository,
) : BaseViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(dataStoreService.getUserData())
    val user = _user.asStateFlow()

    private val _promoCodes: MutableStateFlow<List<PromoCode>> = MutableStateFlow(emptyList())
    val promoCodes = _promoCodes.asStateFlow()

    private val _myAddressesCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val myAddressesCount = _myAddressesCount.asStateFlow()

    private val _myOrdersCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val myOrdersCount = _myOrdersCount.asStateFlow()

    init {
        getOrders()
        viewModelScope.launch {
            firebaseConfigRepository.getPromoCodes()
                .onEach { _promoCodes.value = it }
                .collect()
        }

        firebaseConfigRepository.fetchAddresses()

        firebaseConfigRepository.getAddresses()
            .onEach { _myAddressesCount.value = it.size  }
            .launchIn(viewModelScope)

    }


    fun logOut() {
        viewModelScope.launch {
            dataStoreService.setUserData(User())
        }
    }

    private fun getOrders() {
        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { token ->
                    serviceRepository.getOrders(
                        getOrdersRequest = GetOrdersRequest(
                           phone = _user.value.phone
                        ),
                        token = token.token
                    )
                }.onEach { it ->
                    it?.ordersByOrganizations?.first()?.orders?.let {
                        _myOrdersCount.value = it.size
                    }
                }
                .catch {
                    _showMainError.value = true
                }
                .collect()
        }
    }


    fun addToken(add:Boolean){
        if (add) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    addFcmTokenToFireBase(token)
                }
            }
        } else {
            addFcmTokenToFireBase("")
        }

    }

    private fun addFcmTokenToFireBase(
        token: String,
    ) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference(dataStoreService.getUserData().id)


        userRef.child("fcmToken").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {

                    userRef.child("fcmToken").setValue(token)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { exception ->
                        }
                } catch (_: Exception) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}