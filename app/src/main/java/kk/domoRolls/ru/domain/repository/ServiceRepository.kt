package kk.domoRolls.ru.domain.repository

import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetMenuResponse
import kk.domoRolls.ru.data.model.order.GetStopListResponse
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.GetStreetsRequest
import kk.domoRolls.ru.data.model.order.GetStreetsResponse
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenResponse
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getToken(tokenRequest: ServiceTokenRequest): Flow<ServiceTokenResponse>
    fun getMenuById(getMenuRequest: GetMenuRequest,token: String): Flow<GetMenuResponse>
    fun getStopLists(getStopListRequest: GetStopListRequest,token: String): Flow<GetStopListResponse>
    fun getStreets(getStreetsRequest: GetStreetsRequest,token: String):Flow<GetStreetsResponse>

}