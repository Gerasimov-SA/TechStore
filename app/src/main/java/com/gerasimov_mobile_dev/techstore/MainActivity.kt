package com.gerasimov_mobile_dev.techstore

import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.runtime.rememberCoroutineScope

import androidx.navigation.compose.rememberNavController
import com.gerasimov_mobile_dev.techstore.navigation.TechStoreNavGraph

import com.gerasimov_mobile_dev.techstore.ui.theme.TechStoreTheme

class MainActivity : ComponentActivity() {
    private var mUploadCallback: ValueCallback<Array<Uri>>? = null

    // Лаунчер для загрузки файлов с сайта
    private val filePicker = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val result = if (uris.isNotEmpty()) uris.toTypedArray() else null
        mUploadCallback?.onReceiveValue(result)
        mUploadCallback = null
    }

    fun requestFilePick(
        callback: ValueCallback<Array<Uri>>,
        fileChooserParams: WebChromeClient.FileChooserParams? // параметр оставляем для совместимости, но не используем
    ) {
        mUploadCallback?.onReceiveValue(null)
        mUploadCallback = callback

        // задаём тип: любые файлы
        filePicker.launch("*/*")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechStoreTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                TechStoreNavGraph(
                    navController = navController,
                    viewModelFactory = {
                        TechStoreViewModel(application = application)
                    },
                    scope = scope
                )
            }
        }
    }


}

