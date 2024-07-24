package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetStopListResponse(
    @SerializedName("correlationId")
    val correlationId: String,
    @SerializedName("terminalGroupStopLists")
    val terminalGroupStopLists: List<TerminalGroupStopList>
)

data class TerminalGroupStopList(
    @SerializedName("organizationId")
    val organizationId: String,
    @SerializedName("items")
    val terminalGroup: List<TerminalGroup>
)

data class TerminalGroup(
    @SerializedName("terminalGroupId")
    val terminalGroupId: String,
    @SerializedName("items")
    val items: List<Item>
)

data class Item(
    val balance: Int,
    val productId: String,
    val sizeId: String?,
    val sku: String,
    val dateAdd: String
)
