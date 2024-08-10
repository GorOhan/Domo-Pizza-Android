package kk.domoRolls.ru.domain.model

data class PromoCode(
    val value:String = "",
    val discount: Double = 0.0,
    val minPrice: Int = 0,
    val expirationDate: String = "",
    val isOneTime: Boolean = false
)
