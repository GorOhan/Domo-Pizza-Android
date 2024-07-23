package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetMenuResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
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
    @SerializedName("description")
    val description: String?,
    @SerializedName("buttonImageUrl")
    val buttonImageUrl: String?,
    @SerializedName("headerImageUrl")
    val headerImageUrl: String?,
)

data class MenuItem(
    @SerializedName("sku")
    val sku: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("orderItemType")
    val orderItemType: String?,
)
