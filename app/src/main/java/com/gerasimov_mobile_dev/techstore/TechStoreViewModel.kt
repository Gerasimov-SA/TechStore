package com.gerasimov_mobile_dev.techstore

import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.gerasimov_mobile_dev.techstore.bridge.WebScripts
import com.gerasimov_mobile_dev.techstore.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder


sealed class TechStoreState {
    object Loading : TechStoreState()
    data class Ready(val url: String) : TechStoreState()
    data class Error(val message: String) : TechStoreState()

}


class TechStoreViewModel(val context: Context) {
    private val _uiState = MutableStateFlow<TechStoreState>(TechStoreState.Loading)
    val uiState: StateFlow<TechStoreState> = _uiState.asStateFlow()

    init {
        if (!NetworkUtils.isConnected(context = context)) {
            _uiState.value = TechStoreState.Error("Нет подключения к интернету")
            Log.d("ViewModel", "init")
        }
    }

    fun pageStarted() {


    }


    fun pageFinished(url: String?) {
        if (url != "about:blank" && !url.isNullOrBlank()) {
            _uiState.value = TechStoreState.Ready(url)
        } else {
            Log.w("VM", "Ignored invalid URL: $url")

        }
    }


    fun error(message: String) {

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

    fun reloadWebView() {
        webViewRef?.reload()
    }

    fun retry() {
        if (NetworkUtils.isConnected(context = context)) {
            _uiState.value = TechStoreState.Loading
            reloadWebView()
        } else _uiState.value = TechStoreState.Error("Нет подключения к интернету")
    }

    fun setWebViewUrl(url: String) {
        webViewRef?.loadUrl(url)
    }

    fun evaluateJs(js: String) {
        // Если WebView ещё не готов — можно либо игнорировать, либо ставить в очередь
        webViewRef?.evaluateJavascript(js) { result ->
            Log.d("VM", "JS result: $result")
        }
    }

    private var pendingSearch: String? = null

    // Вызывается из SearchBar при нажатии Enter / кнопки поиска

    fun performSearch(query: String) {
        if (query.isBlank()) {
            setWebViewUrl(BASE_URL)
            return
        }
        val encoded = URLEncoder.encode(query, "UTF-8")
        setWebViewUrl("https://hop.uz/all?search=$encoded")
    }
    // Вызывается из onPageFinished после скрытия шапки

    companion object {
//        const val BASE_URL = "https://gerasimov-sa.github.io/techstore-web/"
        const val BASE_URL = "https://hop.uz/"


    }
}