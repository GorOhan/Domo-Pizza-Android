package kk.domoRolls.ru.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.PromoStory
import kk.domoRolls.ru.domain.model.TimeSlot
import kk.domoRolls.ru.domain.model.address.Address
import kk.domoRolls.ru.domain.model.address.UserFirebase
import kk.domoRolls.ru.domain.model.map.Polygon
import kk.domoRolls.ru.domain.repository.FirebaseConfigRepository
import kk.domoRolls.ru.util.getCurrentWeekdayInRussian
import kk.domoRolls.ru.util.parseToGiftProduct
import kk.domoRolls.ru.util.parseToMapData
import kk.domoRolls.ru.util.parseToPromoCodes
import kk.domoRolls.ru.util.parseToPromos
import kk.domoRolls.ru.util.parseToWorkingHours
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class FirebaseConfigRepositoryImpl(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val dataStoreService: DataStoreService,
) : FirebaseConfigRepository {

    private val _isAppAvailable: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val _promoStoryList: MutableStateFlow<List<PromoStory>> = MutableStateFlow(emptyList())
    private val _workingTimeSlots: MutableStateFlow<List<TimeSlot>> = MutableStateFlow(emptyList())
    private val _promoCodes: MutableStateFlow<List<PromoCode>> = MutableStateFlow(emptyList())
    private val _otpMessage: MutableStateFlow<String> = MutableStateFlow("")
    private val _otpLength: MutableStateFlow<Int> = MutableStateFlow(0)
    private val _termsMessage: MutableStateFlow<String> = MutableStateFlow("")
    private val _offerText: MutableStateFlow<String> = MutableStateFlow("")
    private val _giftProduct: MutableStateFlow<GiftProduct> = MutableStateFlow(GiftProduct())
    private val _mapData: MutableStateFlow<List<Polygon>> = MutableStateFlow(emptyList())
    private val _addresses: MutableStateFlow<List<Address>> = MutableStateFlow(emptyList())
    private val _deliveryTime: MutableStateFlow<String> = MutableStateFlow("")


    init {
        firebaseRemoteConfig.fetch(1)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _deliveryTime.value = firebaseRemoteConfig.getString("delivery_time")
                    _isAppAvailable.value =
                        firebaseRemoteConfig.getBoolean("isAppAvailable")

                    val promoList: String = firebaseRemoteConfig.getString("promo_list")

                    promoList.parseToPromos()?.let {
                        _promoStoryList.value = it
                    }

                    val workingHours: String =
                        firebaseRemoteConfig.getString("working_hours")

                    val hours =
                        workingHours.parseToWorkingHours()?.workingHours?.get(
                            getCurrentWeekdayInRussian()
                        )
                    hours?.let {
                        _workingTimeSlots.value = it
                    }

                    val promoCodes: String = firebaseRemoteConfig.getString("promocodes")

                    promoCodes.parseToPromoCodes()?.let {
                        _promoCodes.value = it
                    }

                    _otpMessage.value = firebaseRemoteConfig.getString("otpMessage")
                    _otpLength.value = firebaseRemoteConfig.getLong("otpLenght").toInt()
                    _termsMessage.value = firebaseRemoteConfig.getString("termsOfUsage_text")
                    _offerText.value = firebaseRemoteConfig.getString("offer_text")

                    val giftProductJson: String =
                        firebaseRemoteConfig.getString("gift_roll")
                    val gift = giftProductJson.parseToGiftProduct() ?: GiftProduct()
                    _giftProduct.value = gift

                    val mapData: String =
                        firebaseRemoteConfig.getString("delivery_polygons")

                    mapData.parseToMapData()?.let {
                        _mapData.value = it
                    }
                }
            }
    }

    override fun getAppAvailable() = _isAppAvailable.asStateFlow()
    override fun getPromoStories() = _promoStoryList.asStateFlow()
    override fun getWorkingHours() = _workingTimeSlots.asStateFlow()
    override fun getPromoCodes() = _promoCodes.asStateFlow()
    override fun getOtpMessage() = _otpMessage.asStateFlow()
    override fun getOtpLength() = _otpLength.asStateFlow()
    override fun getTermsMessage() = _termsMessage.asStateFlow()
    override fun getOfferMessage() = _offerText.asStateFlow()
    override fun getGiftProduct() = _giftProduct.asStateFlow()
    override fun getPolygons() = _mapData.asStateFlow()

    override fun getAddresses() = _addresses.asStateFlow()

    override fun fetchAddresses() {
        fetchUserAddresses(dataStoreService.getUserData().id,
            onSuccess = { addresses ->
                _addresses.value = addresses
            },
            onFailure = { exception ->
                // Handle the error here
                println("Error: ${exception.message}")
            }
        )
    }

    override fun getAddressById(id: String): Address? {
        return _addresses.value.find { it.id == id }
    }

    override fun getDeliveryTime(): StateFlow<String> {
        return _deliveryTime
    }
}

private fun fetchUserAddresses(
    userId: String,
    onSuccess: (List<Address>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference(userId)

    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val user = snapshot.getValue(UserFirebase::class.java)
                val addresses = user?.addresses ?: emptyList()
                onSuccess(addresses)
            } catch (e: Exception) {
                onFailure(e)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            onFailure(error.toException())
        }
    })
}