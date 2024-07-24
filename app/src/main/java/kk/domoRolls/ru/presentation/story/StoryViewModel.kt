package kk.domoRolls.ru.presentation.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.util.parseJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StoryViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
) : ViewModel() {


    private val _promoList: MutableStateFlow<List<Promo>?> = MutableStateFlow(emptyList())
    val promoList = _promoList.asStateFlow()

    init {
        val otpMessage: String = FirebaseRemoteConfig.getInstance().getString("promo_list")

        viewModelScope.launch {
            _promoList.value = otpMessage.parseJson()
        }
    }

}