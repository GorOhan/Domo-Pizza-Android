package kk.domoRolls.ru.domain.model


data class PromoStory(
    val id: Int,
    val name: String,
    val descriptionFull: String,
    val restaurantKey: String,
    val url: String,
    val bannerImage: String,
    val storyImage: String,
    val promoList: String,
    val type: String
)
