package kk.domoRolls.ru.presentation.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.util.parseToPromos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor() : ViewModel() {


    private val _promoStoryList: MutableStateFlow<List<PromoStory>?> = MutableStateFlow(emptyList())
    val promoList = _promoStoryList.asStateFlow()

    init {
        val otpMessage: String = FirebaseRemoteConfig.getInstance().getString("promo_list")

        viewModelScope.launch {
            _promoStoryList.value = otpMessage.parseToPromos()
        }
    }

}