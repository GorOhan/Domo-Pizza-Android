package kk.domoRolls.ru.presentation.onboarding

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.util.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class NotifyPermissionViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
) : BaseViewModel(){

    fun addToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                addFcmTokenToFireBase(token)
            }
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