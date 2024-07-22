package kk.domoRolls.ru.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.auth.SendOTPRequest
import kk.domoRolls.ru.data.model.auth.TokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreService: DataStoreService,
) : ViewModel() {

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _phoneNumber: MutableStateFlow<String> = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _codeInput: MutableStateFlow<String> = MutableStateFlow("")
    val codeInput = _codeInput.asStateFlow()

    private val _token: MutableStateFlow<String> = MutableStateFlow("")
    private val _generatedOtp: MutableStateFlow<String> = MutableStateFlow("")

    private val _otpLength: MutableStateFlow<Int> = MutableStateFlow(0)
    val otpLength = _otpLength.asStateFlow()

    private val _navigateToMain: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val navigateToMain = _navigateToMain.asStateFlow()

    private val _isOtpError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOtpError = _isOtpError.asStateFlow()

    init {
        _otpLength.value = FirebaseRemoteConfig.getInstance().getLong("otpLenght").toInt()
    }

    val isReadyToSendOtp =
        combine(
            _userName,
            _phoneNumber,
        ) { user, phone ->
            (user.isNotBlank()) && (phone.length == 10)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    val isReadyToLogin =
        combine(
            _otpLength,
            _codeInput,
        ) { otpLength, code ->
            otpLength == code.length
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    fun onUserNameInput(input: String) {
        if (input.length < 15) _userName.value = input
    }

    fun onUserPhoneInput(input: String) {
        if (input.length < 11) _phoneNumber.value = input
    }

    fun getToken() {
        viewModelScope.launch {
            authRepository.getToken(
                TokenRequest(
                    user = "KonstantinKiski",
                    pass = "xadrat-2cyxjo-nefFix"
                )
            )
                .onEach {
                    _token.value = it.token
                }
                .catch {

                }
                .collect()
        }
    }

    fun sendOTP() {
        _generatedOtp.value = generateOTP()
        val otpMessage: String = FirebaseRemoteConfig.getInstance().getString("otpMessage")

        viewModelScope.launch {
            if (_phoneNumber.value == "9378852905") return@launch
            authRepository.sendOTP(
                sendOTPRequest = SendOTPRequest(
                    to = "7${_phoneNumber.value}",
                    txt = "$otpMessage ${_generatedOtp.value}"
                ),
                token = _token.value
            )
                .onEach {}
                .catch {}
                .collect()
        }
    }

    fun onOTPInput(input: String) {
        if (input.length <= _otpLength.value) _codeInput.value = input
    }

    private fun generateOTP(): String {
        val digits = "0123456789"
        var otp = ""
        for (i in 0 until _otpLength.value) {
            val randomIndex = Random.nextInt(digits.length)
            val character = digits[randomIndex]
            otp += character
        }
        return otp
    }

    fun verifyOtpCode() {

        viewModelScope.launch {
            if (_generatedOtp.value == _codeInput.value) {
                _navigateToMain.value = true
                isExistUser(phone = _phoneNumber.value) { id ->
                    if (id == null) {
                        saveUser()
                    } else {
                        fillUser(id)
                    }
                }
            } else {
                _isOtpError.emit(true)
            }
        }
    }

    private fun isExistUser(phone: String, completion: (String?) -> Unit) {
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value as? Map<String, Any>
                val id = value?.entries?.firstOrNull { entry ->
                    val user = entry.value as? Map<String, Any>
                    val userPhone = user?.get("phone") as? String
                    phone == userPhone
                }?.key

                completion(id)
            }

            override fun onCancelled(error: DatabaseError) {
                completion(null)
            }
        })
    }

    private fun saveUser() {

        val userID = UUID.randomUUID().toString()

        val ref = FirebaseDatabase.getInstance().reference
        val user = User(id = userID,
            name = userName.value,
            phone = phoneNumber.value)

        ref.child("users").child(userID).setValue(mapOf(
            "username" to user.name,
            "phone" to user.phone,
            "email" to "",
            "birthday" to "",
            "gender" to ""
            ))

        dataStoreService.setUserData(user)
    }

    private fun fillUser(userID:String) {
        val ref = FirebaseDatabase.getInstance().reference

        ref.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                val mapData = value as HashMap<String,String>
                dataStoreService.setUserData(User(
                    id = userID,
                    name = mapData["username"] ?:"",
                    phone = mapData["phone"] ?:""
                ))
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun setOTPError(isError:Boolean){
        _isOtpError.value = isError
    }
}
