package kk.domoRolls.ru.domain.model.address

data class Coordinate(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class Address(
    val building: String = "",
    val city: String = "",
    val comment: String = "",
    val coordinate: Coordinate = Coordinate(),
    val entrance: String = "",
    val flat: String = "",
    val floor: String = "",
    val id: String = "",
    val intercom: String = "",
    val isDefault: Boolean = false,
    val isPrivateHouse: Boolean = false,
    val minDeliveryPrice: Int = 0,
    val street: String = "",
    val streetId: String = "",
    val type: String = ""
)

data class UserFirebase(
    val addresses: List<Address> = listOf(),
    val birthday: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val gender: String = "",
    val phone: String = "",
    val username: String = ""
)