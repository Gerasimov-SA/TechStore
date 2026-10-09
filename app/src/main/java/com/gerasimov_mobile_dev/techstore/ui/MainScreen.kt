package com.gerasimov_mobile_dev.techstore.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gerasimov_mobile_dev.techstore.TechStoreState
import com.gerasimov_mobile_dev.techstore.TechStoreViewModel
import kotlinx.coroutines.coroutineScope

@Composable
fun MainScreen(viewModel: TechStoreViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    when (state) {
        is TechStoreState.Loading -> {
            // Пока грузится страница — показываем Splash
            SplashScreen()
        }
        is TechStoreState.Ready -> {
            // Страница готова — показываем WebView с нужным URL
            WebViewContainer(
                url = (state as TechStoreState.Ready).url,
                onStarted = { viewModel.onPageStarted() },
                onFinished = { loadedUrl -> viewModel.onPageFinished(loadedUrl) },
                onError = { msg -> viewModel.onError(msg) },
                viewModel = viewModel,
                scope = scope
            )
        }
        is TechStoreState.Error -> {
            // Ошибка — показываем понятный экран
            OfflineScreen(
                message = (state as TechStoreState.Error).message,
                onRetry = { /* тут логика повторной загрузки */ }
            )
        }
    }
}