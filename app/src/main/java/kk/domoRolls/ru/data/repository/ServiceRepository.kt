package kk.domoRolls.ru.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kk.domoRolls.ru.data.api.ServiceApi
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.GetOrdersRequest
import kk.domoRolls.ru.data.model.order.GetOrdersResponse
import kk.domoRolls.ru.data.model.order.GetStopListRequest
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.Order
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.domain.model.PromoCode
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.parseToListString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServiceRepositoryImpl(
    private val serviceApi: ServiceApi,
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : ServiceRepository {

    private var currentCategories: MutableStateFlow<List<ItemCategory>> =
        MutableStateFlow(emptyList())
    private var currentMenu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())

    private val hotItems: MutableList<String> = mutableListOf()
    private val newItems: MutableList<String> = mutableListOf()
    private val currentOrders:MutableStateFlow<GetOrdersResponse?> = MutableStateFlow(null)
    private var usedPromoCode:PromoCode? = null



    init {
        firebaseRemoteConfig.fetch(10)
            .addOnCompleteListener { taskFetch ->
                if (taskFetch.isSuccessful) {
                    firebaseRemoteConfig.activate().addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val hots =
                                firebaseRemoteConfig.getString("hot_items").parseToListString()
                                    ?: emptyList()
                            hotItems.addAll(hots)
                            val new = firebaseRemoteConfig.getString("new_items")
                                .parseToListString() ?: emptyList()
                            newItems.addAll(new)
                        }
                    }
                }
            }
    }

    override fun addToCart(menuItem: MenuItem) {
        val indexInMenu = currentMenu.value.indexOfFirst { it.itemId == menuItem.itemId }
        val lists = currentMenu.value.toMutableList()
        val countInCart = currentMenu.value[indexInMenu].countInCart + 1
        lists[indexInMenu] =
            currentMenu.value[indexInMenu].copy(countInCart = countInCart)
        currentMenu.value = lists
    }

    override fun removeFromCart(menuItem: MenuItem) {
        val indexOfItem = currentMenu.value.indexOfFirst { it.itemId == menuItem.itemId }
        val lists = currentMenu.value.toMutableList()
        if (currentMenu.value[indexOfItem].countInCart > 0) {
            val countInCart = currentMenu.value[indexOfItem].countInCart - 1
            lists[indexOfItem] =
                currentMenu.value[indexOfItem].copy(countInCart = countInCart)
        }
        currentMenu.value = lists
    }

    override fun getToken(tokenRequest: ServiceTokenRequest) = emitFlow {
        val token = serviceApi.getToken(tokenRequest)
        return@emitFlow token
    }

    override suspend fun getMenuById(
        newList: List<String>,
        hotList: List<String>,
        disableIds: List<String>,
        getMenuRequest: GetMenuRequest,
        token: String
    ): Flow<List<MenuItem>> {
        currentMenu.value = currentMenu.value.ifEmpty {
            val api = serviceApi.getMenuById(tokenRequest = getMenuRequest, token = "Bearer $token")
            currentCategories.value = api.itemCategories
            api.itemCategories.flatMap { cat ->
                cat.items?.map {
                    it.copy(
                        categoryId = cat.id ?: ""
                    )
                } ?: emptyList()
            }.map { it.copy(isEnable = !disableIds.contains(it.itemId)) }

        }
        if (hotItems.isNotEmpty()) currentMenu.value =
            currentMenu.value.map { it.copy(isHot = hotItems.contains(it.itemId)) }
        if (newItems.isNotEmpty()) currentMenu.value =
            currentMenu.value.map { it.copy(isNew = newItems.contains(it.itemId)) }

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


    override fun getCategories(): Flow<List<ItemCategory>> {
        return currentCategories
    }

    override fun getOrders(
        getOrdersRequest: GetOrdersRequest,
        token: String
    ): Flow<GetOrdersResponse?> = emitFlow {

        return@emitFlow currentOrders.value?.let {
            currentOrders.value
        }?:run {
            val orders = serviceApi.getOrders(getOrdersRequest, token = "Bearer $token")
            currentOrders.value = orders
            currentOrders.value
        }
    }

    override fun getOrderById(id: String): Order? {
        return currentOrders.value?.ordersByOrganizations?.first()?.orders?.find { it.id == id }
    }

    override fun setPromoCode(promoCode: PromoCode) {
        usedPromoCode = promoCode
    }

    override fun getPromoCode() = usedPromoCode

    override fun getCart(): List<MenuItem> = currentMenu.value.filter { it.countInCart>0 }



}
