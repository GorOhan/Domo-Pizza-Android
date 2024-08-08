package kk.domoRolls.ru.data.api


import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetMenuResponse
import kk.domoRolls.ru.data.model.order.GetOrdersRequest
import kk.domoRolls.ru.data.model.order.GetOrdersResponse
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.GetStopListResponse
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.model.order.ServiceTokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ServiceApi {

    @POST("api/1/access_token")
    suspend fun getToken(
        @Body tokenRequest: ServiceTokenRequest,
    ): ServiceTokenResponse

    @POST("api/2/menu/by_id")
    suspend fun getMenuById(
        @Body tokenRequest: GetMenuRequest,
        @Header("Authorization") token: String,
        ): GetMenuResponse

    @POST("api/1/stop_lists")
    suspend fun getStopLists(
        @Body getStopListRequest: GetStopListRequest,
        @Header("Authorization") token: String,
    ): GetStopListResponse

    @POST("api/1/deliveries/by_delivery_date_and_phone")
    suspend fun getOrders(
        @Body getOrdersRequest: GetOrdersRequest,
        @Header("Authorization") token: String,
    ): GetOrdersResponse

}