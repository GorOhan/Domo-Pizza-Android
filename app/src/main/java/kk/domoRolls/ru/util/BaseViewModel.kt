package kk.domoRolls.ru.util


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseViewModel : ViewModel() {


    internal val _showMainError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showMainError = _showMainError.asStateFlow()

    internal val _showContactToSupport: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showContactToSupport = _showContactToSupport.asStateFlow()

    fun hideError() {
        _showMainError.value = false
    }

    fun hideShowContactsError() {
        _showContactToSupport.value = false
    }

}