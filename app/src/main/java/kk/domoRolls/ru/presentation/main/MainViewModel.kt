package kk.domoRolls.ru.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kk.domoRolls.ru.data.model.order.GetMenuRequest
import kk.domoRolls.ru.data.model.order.ItemCategory
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.data.model.order.ServiceTokenRequest
import kk.domoRolls.ru.data.prefs.DataStoreService
import kk.domoRolls.ru.domain.model.Promo
import kk.domoRolls.ru.domain.model.User
import kk.domoRolls.ru.domain.repository.ServiceRepository
import kk.domoRolls.ru.util.getCurrentWeekdayInRussian
import kk.domoRolls.ru.util.isWorkingTime
import kk.domoRolls.ru.util.parseToListString
import kk.domoRolls.ru.util.parseToPromos
import kk.domoRolls.ru.util.parseToWorkingHours
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreService: DataStoreService,
    private val serviceRepository: ServiceRepository,
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : ViewModel() {

    private val _user: MutableStateFlow<User> = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _promoList: MutableStateFlow<List<Promo>> = MutableStateFlow(emptyList())
    val promoList = _promoList.asStateFlow()

    private val _menu: MutableStateFlow<List<MenuItem>> = MutableStateFlow(emptyList())
    val menu = _menu.asStateFlow()

    private val _showLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showLoading = _showLoading.asStateFlow()

    private val _categories: MutableStateFlow<List<ItemCategory>> = MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    private val _isOpen: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isOpen = _isOpen.asStateFlow()

    private var hotList: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private var newList: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    init {

        viewModelScope.launch {
            _showLoading.value = true
            _user.value = dataStoreService.getUserData()
        }

        viewModelScope.launch {
            firebaseRemoteConfig.fetch(10)
                .addOnCompleteListener { taskFetch ->
                    if (taskFetch.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                val hots =
                                    firebaseRemoteConfig.getString("hot_items")
                                        .parseToListString()
                                        ?: emptyList()
                                hotList.value = hots
                                newList.value = firebaseRemoteConfig.getString("new_items")
                                    .parseToListString() ?: emptyList()
                                val promoList: String =
                                    firebaseRemoteConfig.getString("promo_list")
                                val workingHours: String =
                                    firebaseRemoteConfig.getString("working_hours")

                                promoList.parseToPromos()?.let {
                                    _promoList.value = it
                                }
                                val hours =
                                    workingHours.parseToWorkingHours()?.workingHours?.get(
                                        getCurrentWeekdayInRussian()
                                    )
                                _isOpen.value = hours?.let { isWorkingTime(it) } ?: true
                                fetchMenu()
                            }
                        }
                    }
                }
        }



        viewModelScope.launch {
            serviceRepository.getCategories().onEach {
                _categories.value = it
                categoryCheckFirst()
            }.collect()
        }
    }

    fun addToCart(menuItem: MenuItem) {
        serviceRepository.addToCart(menuItem)
    }

    fun removeFromCart(menuItem: MenuItem) {
        serviceRepository.removeFromCart(menuItem)
    }


    fun categoryCheck(menuItem: ItemCategory) {
        val item = categories.value.indexOf(menuItem)
        val lists = categories.value.toMutableList()
        lists.forEach {
            it.isChecked = false
        }
        lists[item] = categories.value[item].copy(isChecked = true)
        _categories.value = lists
    }

    private fun categoryCheckFirst() {
        val item = 0
        val lists = categories.value.toMutableList()
        lists.forEach {
            it.isChecked = false
        }
        if (categories.value.isNotEmpty()) {
            lists[item] = categories.value[item].copy(isChecked = true)
        }

        _categories.value = lists
    }

    fun hideSleepView() {
        _isOpen.value = true
    }

    fun fetchMenu() {
        viewModelScope.launch {
            serviceRepository.getToken(ServiceTokenRequest())
                .flatMapConcat { token ->
                    serviceRepository.getMenuById(
                        hotList = hotList.value,
                        newList = newList.value,
                        getMenuRequest = GetMenuRequest(),
                        token = token.token
                    )
                }
                .collect { menuItems ->
                    _menu.value = menuItems
                    _showLoading.value = false
                }
        }
    }

}