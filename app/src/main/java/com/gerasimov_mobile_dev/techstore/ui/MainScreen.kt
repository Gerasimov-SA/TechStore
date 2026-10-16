package com.gerasimov_mobile_dev.techstore.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gerasimov_mobile_dev.techstore.TechStoreState
import com.gerasimov_mobile_dev.techstore.TechStoreViewModel


@Composable
fun MainScreen(viewModel: TechStoreViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    Log.d("state","state = $state")
    when (val s = state) {
        is TechStoreState.Loading -> {
            Log.d("MainScreen", "State: Loading")
            SplashScreen()
        }
        is TechStoreState.Ready -> {
            Log.d("MainScreen", "State: Ready, url = ${s.url}")
            Log.d("MainScreen", "Loading URL: $s.url")
            WebViewContainer(

                url = s.url,
                onStarted = { viewModel.onPageStarted() },
                onFinished = { loadedUrl -> viewModel.onPageFinished(loadedUrl) },
                onError = { msg -> viewModel.onError(msg) },
                viewModel = viewModel,
                scope = scope
            )

        }
        is TechStoreState.Error -> {
            Log.e("MainScreen", "State: Error, message = ${s.message}")
            OfflineScreen(
                message = s.message,
                onRetry = { /* тут логика повторной загрузки */ }
            )
        }
    }
}