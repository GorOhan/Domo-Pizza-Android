package kk.domoRolls.ru.data.repository

import kk.domoRolls.ru.data.api.ServiceApi
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.GetStreetsRequest
import kk.domoRolls.ru.data.model.order.GetStreetsResponse
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ServiceRepositoryImpl(
    private val serviceApi: ServiceApi,
) : ServiceRepository {

    private var currentMenu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    private var currentCart: MutableList<MenuItem> = mutableListOf()

    override fun addToCart(menuItem: MenuItem){
        currentCart.add(menuItem)
    }

    override fun removeFromCart(menuItem: MenuItem) {
        currentCart.remove(menuItem)
    }

    override fun getCart(): Flow<List<MenuItem>> = emitFlow {
        currentCart
    }


    override fun getToken(tokenRequest: ServiceTokenRequest) = emitFlow {
        val token = serviceApi.getToken(tokenRequest)
        return@emitFlow token
    }

    override fun getMenuById(
        disableIds: List<String>,
        getMenuRequest: GetMenuRequest,
        token: String
    ): Flow<List<MenuItem>> =
        emitFlow {
            currentMenu.value = currentMenu.value.ifEmpty {
                serviceApi.getMenuById(
                    getMenuRequest,
                    token = "Bearer $token"
                ).itemCategories.flatMap { it.items ?: emptyList() }
            }
            currentMenu.value = currentMenu.value.map { it.copy(isEnable = !disableIds.contains(it.itemId))}
            return@emitFlow currentMenu.value
        }

    override fun getStopListsIds(
        getStopListRequest: GetStopListRequest,
        token: String
    ): Flow<List<String>> = emitFlow {
        val products = serviceApi.getStopLists(
            getStopListRequest,
            token = "Bearer $token"
        ).terminalGroupStopLists.first().terminalGroup.first().items.map { it.productId }
        return@emitFlow products
    }

    override fun getStreets(
        getStreetsRequest: GetStreetsRequest,
        token: String
    ): Flow<GetStreetsResponse> = emitFlow {
        val menu = serviceApi.getStreets(getStreetsRequest, token = "Bearer $token")
        return@emitFlow menu
    }
}
