package kk.domoRolls.ru.data.model.sendorder


data class SendOrderData(
    val terminalGroupId: String,
    val organizationId: String,
    val order: SendOrder
)

data class SendOrder(
    val phone: String,
    val orderTypeId: String,
    val items: List<SendItem>,
    val customer: SendCustomer,
    val deliveryPoint: SendDeliveryPoint,
    val guests: SendGuests
)

data class SendItem(
    val type: String,
    val amount: Int,
    val productId: String,
    val price: Int,
    val productSizeId: String
)

data class SendCustomer(
    val name: String
)

data class SendDeliveryPoint(
    val comment: String,
    val address: SendAddress
)

data class SendAddress(
    val flat: String,
    val house: String,
    val entrance: String,
    val floor: String,
    val street: SendStreet,
    val doorphone: String
)

data class SendStreet(
    val id: String
)

data class SendGuests(
    val count: Int
)