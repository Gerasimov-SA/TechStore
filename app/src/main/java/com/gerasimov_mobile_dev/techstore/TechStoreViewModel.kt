package com.gerasimov_mobile_dev.techstore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


sealed class TechStoreState{
    object Loading: TechStoreState()
    data class Ready(val url:String): TechStoreState()
    data class Error(val message: String): TechStoreState()
}


class TechStoreViewModel(initialUrl: String) {
    private val _uiState = MutableStateFlow<TechStoreState>(TechStoreState.Ready(initialUrl))
    val uiState: StateFlow<TechStoreState> = _uiState.asStateFlow()


    // Вызываем, когда WebView начал грузить страницу
    fun onPageStarted() {
        _uiState.value = TechStoreState.Loading
    }

    // Вызываем, когда страница полностью загрузилась
    fun onPageFinished(url: String?) {
        url?.let {
            _uiState.value = TechStoreState.Ready(it)
        } ?: run {
            // Если URL вдруг нет
            _uiState.value = TechStoreState.Error("Не удалось получить URL страницы")
        }
    }

    // Вызываем при ошибке загрузки (onReceivedError / onReceivedHttpError)
    fun onError(message: String) {
        _uiState.value = TechStoreState.Error(message)
    }

    private var _canGoBack = false
    // Если хочешь отдельный поток для canGoBack — можно сделать StateFlow, но для MVP хватит простого поля
    fun updateCanGoBack(canGoBack: Boolean) {
        _canGoBack = canGoBack
    }

    fun goBack(): Boolean {
        return if (_canGoBack) {
            // Тут ты вызовешь webView.goBack() из UI (через колбэк или отдельный метод)
            true
        } else {
            false
        }
    }

}