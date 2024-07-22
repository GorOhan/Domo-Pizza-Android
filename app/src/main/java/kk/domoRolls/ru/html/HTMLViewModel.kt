package kk.domoRolls.ru.html

import androidx.lifecycle.ViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HTMLViewModel @Inject constructor() : ViewModel() {

    private val _content: MutableStateFlow<String> = MutableStateFlow("")
    val content = _content.asStateFlow()

    init {
        _content.value = FirebaseRemoteConfig.getInstance().getString("termsOfUsage_text")
    }
}