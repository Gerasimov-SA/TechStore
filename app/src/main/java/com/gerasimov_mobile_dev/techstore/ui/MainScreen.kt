package com.gerasimov_mobile_dev.techstore.ui

import android.text.Layout
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gerasimov_mobile_dev.techstore.TechStoreState
import com.gerasimov_mobile_dev.techstore.TechStoreViewModel
import com.gerasimov_mobile_dev.techstore.navigation.BottomNavItem
import com.gerasimov_mobile_dev.techstore.navigation.allBottomNavItems


@Composable
fun MainScreen(viewModel: TechStoreViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    // Состояние текущего пункта меню
    var currentItem: BottomNavItem by remember { mutableStateOf(BottomNavItem.Home) }

    BackHandler() {
        viewModel.goBack()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                                is BottomNavItem.Search -> viewModel.setWebViewUrl("https://gerasimov-sa.github.io/techstore-web/#search")
                                is BottomNavItem.Cart -> viewModel.evaluateJs("openCart()")
                                is BottomNavItem.Profile -> viewModel.setWebViewUrl("https://gerasimov-sa.github.io/techstore-web/profile.html")
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(text = item.title) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
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