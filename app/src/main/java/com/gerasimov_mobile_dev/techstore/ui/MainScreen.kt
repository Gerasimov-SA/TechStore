package com.gerasimov_mobile_dev.techstore.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gerasimov_mobile_dev.techstore.TechStoreState
import com.gerasimov_mobile_dev.techstore.TechStoreViewModel


@Composable
fun MainScreen(viewModel: TechStoreViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    BackHandler() {
        viewModel.goBack()
    }
    Box(modifier = Modifier.fillMaxSize()) {

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
            scope = scope
        )

    }

        Log.d("state", "state = $state")
        when (val s = state) {
            is TechStoreState.Loading -> {
                Log.d("MainScreen", "State: Loading")

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