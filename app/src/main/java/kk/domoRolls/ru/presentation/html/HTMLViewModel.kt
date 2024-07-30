package kk.domoRolls.ru.presentation.html

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HTMLViewModel @Inject constructor(
    private val firebaseConfigRepository: FirebaseConfigRepository
) : ViewModel() {

    private val _content: MutableStateFlow<String> = MutableStateFlow("")
    val content = _content.asStateFlow()

    init {
        viewModelScope.launch {
            firebaseConfigRepository.getTermsMessage()
                .onEach { _content.value = it }
                .collect()
        }
    }

    fun setContent(htmlScreenType: HTMLScreenType) {
        viewModelScope.launch {
            when (htmlScreenType) {
                HTMLScreenType.TERMS -> {
                    _content.value = firebaseConfigRepository.getTermsMessage().value
                }

                HTMLScreenType.OFFER -> {
                    _content.value = firebaseConfigRepository.getOfferMessage().value

                }
            }
        }
    }

}