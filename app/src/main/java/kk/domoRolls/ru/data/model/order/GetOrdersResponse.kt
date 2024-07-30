package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetOrdersResponse(
    @SerializedName("correlationId")
    val correlationId: String,
    @SerializedName("ordersByOrganizations")
    val ordersByOrganizations: List<OrdersByOrganizations>
)

data class OrdersByOrganizations(
    @SerializedName("organizationId")
    val organizationId: String,
    @SerializedName("orders")
    val orders: List<Order>
)

data class Order(
    @SerializedName("id")
    val id: String,
    @SerializedName("order")
    val orderItem: OrderItem
)

data class OrderItem(
    @SerializedName("items")
    val id: List<OrderMenuItem>,
    @SerializedName("deliveryPoint")
    val deliveryPoint: DeliveryPoint,
    @SerializedName("status")
    val status: String,
    @SerializedName("sum")
    val sum: Double,
    )

data class OrderMenuItem(
    @SerializedName("product")
    val product: Product,
    @SerializedName("amount")
    val amount: Double,


    )

data class DeliveryPoint(
    @SerializedName("coordinates")
    val coordinates: Coordinates,
)

data class Coordinates(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
)

data class Product(
    @SerializedName("id")
    val id: String
)

enum class OrderStatus(val value: String) {
    UNCONFIRMED("Unconfirmed"),
    WAIT_COOKING("WaitCooking"),
    READY_FOR_COOKING("ReadyForCooking"),
    COOKING_STARTED("CookingStarted"),
    COOKING_COMPLETED("CookingCompleted"),
    WAITING("Waiting"),
    ON_WAY("OnWay"),
    DELIVERED("Delivered"),
    CLOSED("Closed"),
    CANCELLED("Cancelled"),
    DELAYED("Delayed")
}

val OrderStatus.orderStatusTitle: String
    get() = when (this) {
        OrderStatus.WAIT_COOKING, OrderStatus.READY_FOR_COOKING, OrderStatus.COOKING_STARTED -> "Заказ готовят"
        OrderStatus.COOKING_COMPLETED, OrderStatus.WAITING -> "Приготовлен"
        OrderStatus.ON_WAY -> "Едет к вам"
        OrderStatus.UNCONFIRMED, OrderStatus.CANCELLED, OrderStatus.CLOSED -> "Отменен"
        OrderStatus.DELIVERED -> "Доставлен"
        OrderStatus.DELAYED -> "Заказ доставят к"
    }

val OrderStatus.OrderStepCount: Int
    get() = when (this) {
        OrderStatus.WAIT_COOKING, OrderStatus.READY_FOR_COOKING, OrderStatus.COOKING_STARTED -> 1
        OrderStatus.COOKING_COMPLETED, OrderStatus.WAITING -> 2
        OrderStatus.ON_WAY -> 3
        OrderStatus.DELIVERED -> 4
        OrderStatus.UNCONFIRMED, OrderStatus.CANCELLED, OrderStatus.CLOSED, OrderStatus.DELAYED -> 0
    }
