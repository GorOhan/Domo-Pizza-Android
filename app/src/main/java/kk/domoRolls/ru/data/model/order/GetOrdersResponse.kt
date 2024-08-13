package kk.domoRolls.ru.data.model.order

import com.google.gson.annotations.SerializedName

data class GetOrdersResponse(
    @SerializedName("correlationId")
    val correlationId: String?,
    @SerializedName("ordersByOrganizations")
    val ordersByOrganizations: List<OrdersByOrganizations>?
)

data class OrdersByOrganizations(
    @SerializedName("organizationId")
    val organizationId: String?,
    @SerializedName("orders")
    val orders: List<Order>?
)

data class Order(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("order")
    val orderItem: OrderItem? = null,
    @SerializedName("creationStatus")
    val creationStatus: String? = null,
    val orderDeliveryTime: String = ""
)

data class OrderItem(
    @SerializedName("items")
    val items: List<OrderMenuItem>?,
    @SerializedName("deliveryPoint")
    val deliveryPoint: DeliveryPoint?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("whenCreated")
    val whenCreated: String?,
    @SerializedName("sum")
    val sum: Double?,
    )

data class OrderMenuItem(
    @SerializedName("product")
    val product: Product?,
    @SerializedName("amount")
    val amount: Double?,


    )

data class DeliveryPoint(
    @SerializedName("coordinates")
    val coordinates: Coordinates?,
    @SerializedName("address")
    val deliveryAddress: DeliveryAddress?,
)

data class DeliveryAddress(
    @SerializedName("house")
    val house: String?,
    @SerializedName("street")
    val street: DeliveryStreet?,
)

data class DeliveryStreet(
    @SerializedName("name")
    val name: String?,
)

data class Coordinates(
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
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


fun OrderStatus.OrderDeliveryTime(time:Int) = when(this){
        OrderStatus.WAIT_COOKING, OrderStatus.READY_FOR_COOKING, OrderStatus.COOKING_STARTED -> "~$time мин"
        OrderStatus.COOKING_COMPLETED, OrderStatus.WAITING -> "~${time*0.65} мин"
        OrderStatus.ON_WAY -> "~${time*0.35} мин"
        OrderStatus.DELIVERED -> "~0 мин"
        OrderStatus.UNCONFIRMED, OrderStatus.CANCELLED, OrderStatus.CLOSED, OrderStatus.DELAYED -> "---"
    }

val OrderStatus.showInMainPage: Boolean
    get() = when (this) {
        OrderStatus.WAIT_COOKING, OrderStatus.READY_FOR_COOKING, OrderStatus.COOKING_STARTED -> true
        OrderStatus.COOKING_COMPLETED, OrderStatus.WAITING -> true
        OrderStatus.ON_WAY -> true
        OrderStatus.UNCONFIRMED, OrderStatus.CANCELLED, OrderStatus.CLOSED -> false
        OrderStatus.DELIVERED -> false
        OrderStatus.DELAYED -> true
    }

fun String.StatusOfOrder() = OrderStatus.entries.find { it.value == this}


fun iikoBody(additionalComment: String?): Map<String, Any> {
    val body = mutableMapOf<String, Any>(
        "organizationId" to ("03a1584e-1c80-4071-829d-997688b68cba"),
        "terminalGroupId" to ("terminalGroupId" ?: ""),
        "order" to mutableMapOf(
            "phone" to ("+79378852905"),
            "orderTypeId" to ("DOSTAVKA" ?: ""),
            "deliveryPoint" to (DeliveryPoint(null,null)),
            "customer" to mapOf(
                "name" to (" ТЕСТ! НЕ ГОТОВИТЬ!")
            ),
            "items" to emptyList<MenuItem>(),
            "guests" to mapOf("count" to 4),

        )
    )

    additionalComment?.let {
        (body["order"] as MutableMap<String, Any>)["comment"] = it
    }
    return body

}