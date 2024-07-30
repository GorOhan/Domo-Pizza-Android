package kk.domoRolls.ru.domain.repository

import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.model.TimeSlot
import kotlinx.coroutines.flow.StateFlow

interface FirebaseConfigRepository {

    fun getAppAvailable():StateFlow<Boolean>
    fun getPromoStories():StateFlow<List<PromoStory>>
    fun getWorkingHours():StateFlow<List<TimeSlot>>
    fun getPromoCodes():StateFlow<List<PromoCode>>
    fun getOtpMessage():StateFlow<String>
    fun getOtpLength():StateFlow<Int>
    fun getTermsMessage():StateFlow<String>
    fun getOfferMessage():StateFlow<String>
    fun getGiftProduct():StateFlow<GiftProduct>



}