package kk.domoRolls.ru.data.model.sendorder

import com.google.gson.annotations.SerializedName
import kk.domoRolls.ru.data.model.order.DeliveryPoint
import kk.domoRolls.ru.data.model.order.MenuItem

data class SendOrder(
    val phone: String,
//    val deliveryPoint: DeliveryPoint,
//    val orderTypeId: String,
//    val customer: Customer,
  val items: List<SendOrderItem>,
//    val guests: Guests
)


data class Customer(
    val name: String
)

data class Guests(
    val count: Int
)

data class SendOrderRequest(
    val organizationId: String,
    val terminalGroupId: String,
    val order: SendOrder
)

data class SendOrderItem(
    val amount: Double = 0.0,
    val productSizeId: String = ""
)
