package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetMenuResponse(
//    @SerializedName("id")
//    val id: String?,
//    @SerializedName("name")
//    val name: String?,
//    @SerializedName("description")
//    val description: String?,
    @SerializedName("itemCategories")
    val itemCategories: List<ItemCategory>
    )

data class ItemCategory(
    @SerializedName("items")
    val items: List<MenuItem>?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    var isChecked:Boolean = false,
//    @SerializedName("description")
//    val description: String?,
//    @SerializedName("buttonImageUrl")
//    val buttonImageUrl: String?,
//    @SerializedName("headerImageUrl")
//    val headerImageUrl: String?,
)

data class MenuItem(
    @SerializedName("itemSizes")
    val itemSizes: List<MenuItemSize>? = emptyList(),
    @SerializedName("itemId")
    val itemId: String? = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String? = "",
    val isEnable:Boolean = true,
    val countInCart:Int = 0,
    val categoryId: String = "",
    val isHot:Boolean = false,
    val isNew: Boolean = false,
)

data class MenuItemSize(
    @SerializedName("prices")
    val prices: List<ItemPrice>?,
    @SerializedName("portionWeightGrams")
    val portionWeightGrams: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("buttonImageUrl")
    val buttonImageUrl: String?,
)

data class ItemPrice(
    @SerializedName("organizationId")
    val organizationId: String?,
    @SerializedName("price")
    val price: Double?,

)
