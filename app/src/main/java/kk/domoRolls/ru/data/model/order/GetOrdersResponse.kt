package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetOrdersResponse(
    @SerializedName("correlationId")
    val correlationId: String,
    @SerializedName("ordersByOrganizations")
    val ordersByOrganizations:List<OrdersByOrganizations>
)

data class OrdersByOrganizations(
    @SerializedName("organizationId")
    val organizationId:String,
    @SerializedName("orders")
    val orders: List<Order>
)

data class Order(
    @SerializedName("id")
    val id:String,
    @SerializedName("posId")
    val posId:String,
    @SerializedName("order")
    val orderItem:OrderItem
)

data class OrderItem(
    @SerializedName("items")
    val id:List<OrderMenuItem>,
)

data class OrderMenuItem(
    @SerializedName("size")
    val id:MenuItemSize,
)