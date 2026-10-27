package com.gerasimov_mobile_dev.techstore

import android.app.Application
import android.util.Log
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.gerasimov_mobile_dev.techstore.bridge.WebScripts
import com.gerasimov_mobile_dev.techstore.navigation.BottomNavItem
import com.gerasimov_mobile_dev.techstore.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URLEncoder


sealed class TechStoreState {
    object Loading : TechStoreState()
    data class Ready(val url: String) : TechStoreState()
    data class Error(val message: String) : TechStoreState()

}


class TechStoreViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<TechStoreState>(TechStoreState.Loading)
    val uiState: StateFlow<TechStoreState> = _uiState.asStateFlow()
    private val _currentTab = MutableStateFlow<BottomNavItem>(BottomNavItem.Home)
    val currentTab: StateFlow<BottomNavItem> = _currentTab.asStateFlow()

    init {
        if (!NetworkUtils.isConnected(context = application)) {
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
        if (NetworkUtils.isConnected(application)) {
            _uiState.value = TechStoreState.Loading
            reloadWebView()
        } else _uiState.value = TechStoreState.Error("Нет подключения к интернету")
    }

    fun setWebViewUrl(url: String) {
        webViewRef?.loadUrl(url)
    }



    fun performSearch(query: String) {
        if (query.isBlank()) {
            setWebViewUrl(BASE_URL)
            return
        }
        val encoded = URLEncoder.encode(query, "UTF-8")
        setWebViewUrl("https://hop.uz/all?search=$encoded")
    }

    fun onSuggestionSelected(suggestion: String) {
        _currentTab.value = BottomNavItem.Home
        performSearch(suggestion)
    }

    fun onTabSelected(item: BottomNavItem) {
        _currentTab.value = item
        setWebViewUrl(item.url)
    }

    //Поиск с подскзаками с сайта
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()
    private var suggestJob: Job? = null

    fun onQueryChanged(query: String) {
        suggestJob?.cancel()
        if (query.length < 2) {
            _suggestions.value = emptyList()
            return
        }
        suggestJob = viewModelScope.launch {
            delay(300)
            val wv = webViewRef ?: return@launch

            // Вводим текст в родное поле поиска веб сайта
            withContext(Dispatchers.Main) {
                wv.evaluateJavascript(WebScripts.typeInSiteSearch(query), null)
            }
            //Даём сайту время
            delay(600)
            //Читаем подсказки поиска из сайта
            withContext(Dispatchers.Main) {
                wv.evaluateJavascript(WebScripts.readSearchSuggestions) { result ->
                    _suggestions.value = parseSuggestions(result)
                }
            }
        }
    }

    fun onSearchSubmitted(query: String) {
        _currentTab.value = BottomNavItem.Home
        performSearch(query)
    }

    fun onSearchCleared() {
        suggestJob?.cancel()
        _suggestions.value = emptyList()
    }

    private fun parseSuggestions(raw: String?): List<String> = try {
        val cleaned = raw?.trim()?.removeSurrounding("\"")?.replace("\\\"", "\"") ?: "[]"
        val arr = JSONArray(cleaned)
        (0 until arr.length()).map { arr.getString(it) }
    } catch (e: Exception) {
        Log.w("VM", "parseSuggestions failed: ${e.message}")
        emptyList()
    }

    companion object {
        //        const val BASE_URL = "https://gerasimov-sa.github.io/techstore-web/"
        const val BASE_URL = "https://hop.uz/"


    }
}