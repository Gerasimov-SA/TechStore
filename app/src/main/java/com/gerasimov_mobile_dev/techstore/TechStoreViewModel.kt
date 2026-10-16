package com.gerasimov_mobile_dev.techstore

import android.webkit.WebView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


sealed class TechStoreState {
    object Loading : TechStoreState()
    data class Ready(val url: String) : TechStoreState()
    data class Error(val message: String) : TechStoreState()

}


class TechStoreViewModel(initialUrl: String) {
    private val _uiState = MutableStateFlow<TechStoreState>(TechStoreState.Loading)
    val uiState: StateFlow<TechStoreState> = _uiState.asStateFlow()

    fun onPageStarted() {

        _uiState.value = TechStoreState.Loading

    }


    fun onPageFinished(url: String?) {
        if (!url.isNullOrEmpty()) {
            _uiState.value = TechStoreState.Ready(url)
        } else {
            // Если URL пустой
            _uiState.value = TechStoreState.Error("Страница загружена, но URL не определён")
        }
    }


    fun onError(message: String) {
        _uiState.value = TechStoreState.Error(message)

    }

   var goBack = false
    fun updateCanGoBack(canGoBack: Boolean) {
        goBack = canGoBack
    }
    private var webViewRef: WebView? = null

    fun setWebView(webView: WebView) {
        webViewRef = webView
    }

    fun goBack(): Unit? {
        return webViewRef?.let { wv ->
            if (wv.canGoBack()) {
                wv.goBack()
            }
        }
    }
}