package kk.domoRolls.ru.domain.repository

import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.GetStreetsRequest
import kk.domoRolls.ru.data.model.order.GetStreetsResponse
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenResponse
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getToken(tokenRequest: ServiceTokenRequest): Flow<ServiceTokenResponse>
    suspend fun getMenuById(disableIds: List<String> = emptyList(), getMenuRequest: GetMenuRequest, token: String): Flow<List<MenuItem>>
    fun getStopListsIds(getStopListRequest: GetStopListRequest,token: String): Flow<List<String>>
    fun getStreets(getStreetsRequest: GetStreetsRequest,token: String):Flow<GetStreetsResponse>

    fun addToCart(menuItem: MenuItem)
    fun removeFromCart(menuItem: MenuItem)
    fun getCart():Flow<List<MenuItem>>
    fun getCategories():Flow<List<ItemCategory>>


}