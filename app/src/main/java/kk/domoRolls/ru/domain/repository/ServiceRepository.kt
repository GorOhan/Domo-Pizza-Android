package kk.domoRolls.ru.domain.repository

import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetOrderByIdRequest
import kk.domoRolls.ru.data.model.order.GetOrdersRequest
import kk.domoRolls.ru.data.model.order.GetOrdersResponse
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.Order
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenResponse
import kk.domoRolls.ru.data.model.sendorder.SendOrderData
import kk.domoRolls.ru.data.model.sendorder.SendOrderResponse
import kk.domoRolls.ru.domain.model.GiftProduct
import kk.domoRolls.ru.domain.model.PromoCode
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getToken(tokenRequest: ServiceTokenRequest): Flow<ServiceTokenResponse>
    suspend fun getMenuById(
        newList: List<String> = emptyList(),
        hotList: List<String> = emptyList(),
        disableIds: List<String> = emptyList(),
        getMenuRequest: GetMenuRequest,
        token: String
    ): Flow<List<MenuItem>>

    fun getStopListsIds(getStopListRequest: GetStopListRequest, token: String): Flow<List<String>>
    fun addToCart(menuItem: MenuItem)
    fun removeFromCart(menuItem: MenuItem)
    fun getCategories(): Flow<List<ItemCategory>>

    fun getOrders(
        updateData: Boolean = false,
        getOrdersRequest: GetOrdersRequest,
        token: String
    ): Flow<GetOrdersResponse?>

    fun setPromoCode(usedPromoCode: PromoCode)

    fun getUsedPromoCode(): PromoCode?

    fun getCart(): List<MenuItem>

    fun sendOrder(sendOrderData: SendOrderData, token: String): Flow<SendOrderResponse>

    fun getOrderById(
        getOrderByIdRequest: GetOrderByIdRequest,
        token: String
    ): Flow<Order?>

    fun setDeviceCount(count: Int)
    fun getDeviceCount(): Flow<Int>

    fun setGiftProduct(gift: GiftProduct?)
    fun getGiftProduct(): GiftProduct?


    fun resetCart()


}