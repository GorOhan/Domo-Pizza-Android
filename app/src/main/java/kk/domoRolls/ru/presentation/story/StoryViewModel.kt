package kk.domoRolls.ru.presentation.story

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.util.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    val firebaseConfigRepository: FirebaseConfigRepository,
) : BaseViewModel() {

    private val _promoStoryList: MutableStateFlow<List<PromoStory>> = MutableStateFlow(emptyList())
    val promoList = _promoStoryList.asStateFlow()

    init {

        firebaseConfigRepository.getPromoStories()
            .onEach { _promoStoryList.value = it }
            .launchIn(viewModelScope)
    }
}