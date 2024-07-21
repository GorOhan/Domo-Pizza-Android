package kk.domoRolls.ru.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.auth.SendOTPRequest
import kk.domoRolls.ru.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _toLoginScreen: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val toLoginScreen = _toLoginScreen.asSharedFlow()

    private val _toMainScreen: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val toMainScreen = _toMainScreen.asSharedFlow()

    private val _toOnBoardingScreen: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val toOnBoardingScreen = _toOnBoardingScreen.asSharedFlow()


    init {

//        viewModelScope.launch {
//            authRepository.getToken(
//                TokenRequest(user = "KonstantinKiski", pass = "xadrat-2cyxjo-nefFix")
//            )
//                .onEach {
//                    sendOTP("Bearer Token ${it.token}")
//                }
//                .catch {
//
//                }
//                .collect()
//        }
    }

    private fun sendOTP(token: String) {
        var test: String = FirebaseRemoteConfig.getInstance().getString("otpMessage")

        viewModelScope.launch {
            authRepository.sendOTP(
                sendOTPRequest = SendOTPRequest(
                    to = "79378852905",
                    txt = "$test ${generateOTP()}"
                ),
                token = token
            )
                .onEach {

                }
                .catch {

                }
                .collect()
        }
    }
}

fun generateOTP(): String {
    val digits = "0123456789"
    var otp = ""
    for (i in 0 until 6) {
        val randomIndex = Random.nextInt(digits.length)
        val character = digits[randomIndex]
        otp += character
    }
    return otp
}