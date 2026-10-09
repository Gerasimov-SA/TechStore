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



    fun onPageStarted() {
        _uiState.value = TechStoreState.Loading
    }


    fun onPageFinished(url: String?) {
        url?.let {
            _uiState.value = TechStoreState.Ready(it)
        } ?: run {

            _uiState.value = TechStoreState.Error("Не удалось получить URL страницы")
        }
    }


    fun onError(message: String) {
        _uiState.value = TechStoreState.Error(message)
    }

    private var _canGoBack = false

    fun updateCanGoBack(canGoBack: Boolean) {
        _canGoBack = canGoBack
    }

    fun goBack(): Boolean {
        return if (_canGoBack) {

            true
        } else {
            false
        }
    }

}