package kk.domoRolls.ru.presentation.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kk.domoRolls.ru.data.model.order.MenuItem
import kk.domoRolls.ru.presentation.components.DomoToolbar
import kk.domoRolls.ru.presentation.components.MenuItemComponent
import kk.domoRolls.ru.presentation.theme.DomoTheme

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel()
) {
    CartScreenUI(
        currentCart = viewModel.currentCart.collectAsState()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartScreenUI(
    currentCart: State<List<MenuItem>> = mutableStateOf(emptyList())
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        stickyHeader {
            DomoToolbar()
        }

        itemsIndexed(currentCart.value) { index, item ->
            MenuItemComponent(menuItem = item)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    DomoTheme {
        CartScreenUI()
    }
}
