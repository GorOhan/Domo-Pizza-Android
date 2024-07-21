package kk.domoRolls.ru.registration

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor() : ViewModel() {

    private val _toLoginScreen: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val toLoginScreen = _toLoginScreen.asSharedFlow()

    private val _toMainScreen: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val toMainScreen = _toMainScreen.asSharedFlow()

    private val _toOnBoardingScreen: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val toOnBoardingScreen = _toOnBoardingScreen.asSharedFlow()


    init {

    }
}