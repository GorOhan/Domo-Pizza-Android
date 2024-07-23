package kk.domoRolls.ru.data.repository

import kk.domoRolls.ru.data.api.ServiceApi
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetMenuResponse
import kk.domoRolls.ru.data.model.order.GetStopListResponse
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.GetStreetsRequest
import kk.domoRolls.ru.data.model.order.GetStreetsResponse
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow

class ServiceRepositoryImpl(
    private val serviceApi: ServiceApi,
) : ServiceRepository {
    override fun getToken(tokenRequest: ServiceTokenRequest) = emitFlow {
        val token = serviceApi.getToken(tokenRequest)
        return@emitFlow token
    }

    override fun getMenuById(getMenuRequest: GetMenuRequest,token:String): Flow<GetMenuResponse>  = emitFlow {
        val menu = serviceApi.getMenuById(getMenuRequest, token = "Bearer $token" )
        return@emitFlow menu
    }

    override fun getStopLists(
        getStopListRequest: GetStopListRequest,
        token: String
    ): Flow<GetStopListResponse>  = emitFlow {
        val menu = serviceApi.getStopLists(getStopListRequest, token = "Bearer $token" )
        return@emitFlow menu
    }

    override fun getStreets(getStreetsRequest: GetStreetsRequest,token: String): Flow<GetStreetsResponse>  = emitFlow {
        val menu = serviceApi.getStreets(getStreetsRequest, token = "Bearer $token" )
        return@emitFlow menu
    }
}
