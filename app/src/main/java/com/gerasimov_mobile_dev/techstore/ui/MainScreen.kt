package com.gerasimov_mobile_dev.techstore.ui

import android.text.Layout
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp


import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gerasimov_mobile_dev.techstore.TechStoreState
import com.gerasimov_mobile_dev.techstore.TechStoreViewModel
import com.gerasimov_mobile_dev.techstore.navigation.BottomNavItem
import com.gerasimov_mobile_dev.techstore.navigation.allBottomNavItems

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TechStoreViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var currentItem: BottomNavItem by remember { mutableStateOf(BottomNavItem.Home) }// Состояние текущего пункта меню нижней навигации

    // Состояние SearchBar (новый API Material 3)
    val searchBarState = rememberSearchBarState()
    // Состояние текста: survive рекомпозиции, история добавим позже
    val textFieldState = rememberTextFieldState()

    BackHandler() {
        viewModel.goBack()
    }
    val inputField = @Composable {
        SearchBarDefaults.InputField(
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = { query ->
                scope.launch { searchBarState.animateToCollapsed() }
                //*логика поиска
            },
            placeholder = { Text("Поиск по названию") },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(
                        onClick = {
                            scope.launch { searchBarState.animateToCollapsed() }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Свернуть"
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск"
                    )
                }
            },
            trailingIcon = {
                // В конце строки — уведомления с бейджем
                BadgedBox(
                    badge = {
                        if (true /*логика бейджей уведомлений*/) {
                            Badge()
                        }
                    }
                ) {
                    IconButton(onClick = { /*логика уведомлений*/ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Уведомления"
                        )
                    }
                }
            }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 72.dp)) {
            //Топ бар с поиском


            //WebView
            WebViewContainer(
                url = when (val s = state) {
                    is TechStoreState.Ready -> s.url
                    is TechStoreState.Loading -> TechStoreViewModel.BASE_URL
                    is TechStoreState.Error -> "about:blank"
                },
                onStarted = { viewModel.pageStarted() },
                onFinished = { loadedUrl -> viewModel.pageFinished(loadedUrl) },
                onError = { msg -> viewModel.error(msg) },
                viewModel = viewModel,
                scope = scope,
                modifier = Modifier.weight(1f)
            )

            //Нижняя панель навигации
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface

            ) {
                allBottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentItem.route == item.route,
                        onClick = {
                            currentItem = item
                            when (item) {
                                is BottomNavItem.Home -> viewModel.setWebViewUrl("https://gerasimov-sa.github.io/techstore-web/")
                                is BottomNavItem.Search -> viewModel.setWebViewUrl("https://gerasimov-sa.github.io/techstore-web/search")
                                is BottomNavItem.Cart -> viewModel.setWebViewUrl("https://gerasimov-sa.github.io/techstore-web/cart")
                                is BottomNavItem.Profile -> viewModel.setWebViewUrl("https://gerasimov-sa.github.io/techstore-web/profile")
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(text = item.title) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding(),
            state = searchBarState,
            inputField = inputField,
        )
    }

    Log.d("state", "state = $state")
    when (val s = state) {
        is TechStoreState.Loading -> {
            Log.d("MainScreen", "State: Loading")
            LoadingScreen()
        }

        is TechStoreState.Error -> {
            Log.e("MainScreen", "State: Error, message = ${s.message}")
            ErrorScreen(
                message = s.message,
                onRetry = { viewModel.retry() }
            )
        }

        else -> Unit
    }


}