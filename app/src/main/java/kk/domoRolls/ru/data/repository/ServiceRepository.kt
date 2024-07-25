package kk.domoRolls.ru.data.repository

import kk.domoRolls.ru.data.api.ServiceApi
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.GetStreetsRequest
import kk.domoRolls.ru.data.model.order.GetStreetsResponse
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ServiceRepositoryImpl(
    private val serviceApi: ServiceApi,
) : ServiceRepository {

    private var currentCategories: MutableStateFlow<List<ItemCategory>> =
        MutableStateFlow(emptyList())
    private var currentMenu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    private var currentCart: MutableList<MenuItem> = mutableListOf()

    override fun addToCart(menuItem: MenuItem) {
        val item = currentMenu.value.indexOf(menuItem)
        val lists = currentMenu.value.toMutableList()
        lists[item] =
            currentMenu.value[item].copy(countInCart = currentMenu.value[item].countInCart + 1)
        currentCart.add(menuItem)
        currentMenu.value = lists
    }

    override fun removeFromCart(menuItem: MenuItem) {
        val item = currentMenu.value.indexOf(menuItem)
        val lists = currentMenu.value.toMutableList()
        lists[item] =
            currentMenu.value[item].copy(countInCart = currentMenu.value[item].countInCart - 1)
        currentCart.remove(menuItem)
        currentMenu.value = lists
    }

    override fun getCart(): Flow<List<MenuItem>> = emitFlow {
        currentCart
    }


    override fun getToken(tokenRequest: ServiceTokenRequest) = emitFlow {
        val token = serviceApi.getToken(tokenRequest)
        return@emitFlow token
    }

    override suspend fun getMenuById(
        disableIds: List<String>,
        getMenuRequest: GetMenuRequest,
        token: String
    ): Flow<List<MenuItem>> {
        currentMenu.value = currentMenu.value.ifEmpty {
            val api = serviceApi.getMenuById(tokenRequest = getMenuRequest, token = "Bearer $token")
            currentCategories.value = api.itemCategories
            api.itemCategories.flatMap { cat -> cat.items?.map { it.copy(categoryId = cat.id?:"") } ?: emptyList() }
        }
        currentMenu.value =
            currentMenu.value.map { it.copy(isEnable = !disableIds.contains(it.itemId)) }
        return currentMenu
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

    override fun getCategories(): Flow<List<ItemCategory>> {
        return currentCategories
    }


}
