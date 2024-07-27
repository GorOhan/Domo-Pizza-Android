package kk.domoRolls.ru.presentation.html

import androidx.lifecycle.ViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HTMLViewModel @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : ViewModel() {

    private val _content: MutableStateFlow<String> =
        MutableStateFlow(firebaseRemoteConfig.getString("termsOfUsage_text"))
    val content = _content.asStateFlow()

}