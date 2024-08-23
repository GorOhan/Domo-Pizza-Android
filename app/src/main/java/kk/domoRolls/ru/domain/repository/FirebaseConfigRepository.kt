package kk.domoRolls.ru.domain.repository

import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.model.TimeSlot
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.model.map.Polygon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FirebaseConfigRepository {

    fun getAppAvailable():StateFlow<Boolean?>
    fun getPromoStories():StateFlow<List<PromoStory>>
    fun getWorkingHours():StateFlow<List<TimeSlot>>
    fun getPromoCodes():StateFlow<List<PromoCode>>
    fun getOtpMessage():StateFlow<String>
    fun getOtpLength():StateFlow<Int>
    fun getTermsMessage():StateFlow<String>
    fun getOfferMessage():StateFlow<String>
    fun getGiftProduct():StateFlow<GiftProduct>
    fun getPolygons():StateFlow<List<Polygon>>
    fun getAddresses(): Flow<List<Address>>
    fun fetchAddresses()
    fun getAddressById(id: String):Address?
    fun getDeliveryTime():StateFlow<String>
    fun getUsedPromoCodes():StateFlow<List<String>>

    fun fetchAllData()
}