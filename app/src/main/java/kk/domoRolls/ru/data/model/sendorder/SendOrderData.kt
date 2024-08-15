package kk.domoRolls.ru.data.model.sendorder

import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.model.address.Address

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

fun createSendOrderData(
    user:User,
    currentCart:List<MenuItem>,
    defaultAddress:Address,
    additionalComment: String,
    usedPromoCode: PromoCode,
    deviceCount: Int,
    pickedTime:String,
    giftProduct: GiftProduct?,

)= SendOrderData(
    terminalGroupId = "dbd89055-96a1-4223-81ba-40afe53bbd04",
    organizationId = "03a1584e-1c80-4071-829d-997688b68cba",
    order = SendOrder(
        phone = "+7${user.phone}",
        orderTypeId = "76067ea3-356f-eb93-9d14-1fa00d082c4e",
        items = currentCart.map {
            SendItem(
                type = "Product",
                amount = it.countInCart,
                productId = it.itemId ?: "",
                price = (it.itemSizes?.first()?.prices?.first()?.price)?.toInt() ?: 0,
                productSizeId = ""
            )
        },
        customer = SendCustomer(
            name = "ТЕСТ! НЕ ГОТОВИТЬ"
        ),
        deliveryPoint = SendDeliveryPoint(
            comment = "${defaultAddress.street}\n " +
                    " $additionalComment\n" +
                    " ${usedPromoCode.value}\n " +
                    "$pickedTime\n" +
                    "${giftProduct?.let { "Ролл в подарок!" }}",
            address = SendAddress(
                flat = defaultAddress.flat,
                house = defaultAddress.privateHouse.toString(),
                entrance = "",
                floor = defaultAddress.floor,
                street = SendStreet(
                    id = ""
                ),
                doorphone = defaultAddress.intercom
            )
        ),
        guests = SendGuests(
            count = deviceCount
        )
    )
)