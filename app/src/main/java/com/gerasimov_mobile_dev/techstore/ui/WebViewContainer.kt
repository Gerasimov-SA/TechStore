package com.gerasimov_mobile_dev.techstore.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.gerasimov_mobile_dev.techstore.MainActivity
import com.gerasimov_mobile_dev.techstore.TechStoreViewModel
import com.gerasimov_mobile_dev.techstore.bridge.WebScripts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WebViewContainer(
    url: String,
    onStarted: () -> Unit,
    onFinished: (String?) -> Unit,
    onError: (String) -> Unit,
    viewModel: TechStoreViewModel,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? MainActivity ?: return

    AndroidView(
        factory = { context ->
            val wv = WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = false
                    displayZoomControls = false
                    allowFileAccess = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    mediaPlaybackRequiresUserGesture = false
                }
//                // Если сайт пытается скачать файл — открываем в браузере/файловом менеджере
                setDownloadListener { downloadUrl, userAgent, contentDisposition, mimeType, contentLength ->
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(downloadUrl)
                    }
                    context.startActivity(intent)
                }
                // WebViewClient — навигация и ошибки
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        Log.d("WebView", "onPageStarted: $url")
                        onStarted()
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        Log.d("WebView", "onPageFinished: $url")

                        view?.evaluateJavascript(WebScripts.hideHeaderAndSearch, null) //Чтобы при загрузке сайта скрыть его шапку

                        viewModel.applyPendingSearch()//
                        onFinished(url)
                        scope.launch(Dispatchers.Main) {
                            viewModel.updateCanGoBack(view?.canGoBack() ?: false)
                        }
                        super.onPageFinished(view, url)
                    }

                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        Log.e(
                            "WebView",
                            "onReceivedError: ${error?.errorCode} ${error?.description} ${request?.url}"
                        )
                        val msg = error?.description ?: "Ошибка загрузки страницы"
                        onError(msg.toString())
                        super.onReceivedError(view, request, error)
                    }


                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return false
                        Log.d("WebView","equest?.url = $url")
                        if (!url.contains(TechStoreViewModel.BASE_URL)) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            view?.context?.startActivity(intent)
                            return true
                        }
                        return false
                    }



                }

//                 ChromeClient — диалоги, загрузки, камера и т.д.
                webChromeClient = object : WebChromeClient() {
                    // Обработка alert/confirm/prompt
                    override fun onJsAlert(
                        view: WebView?,
                        url: String?,
                        message: String?,
                        result: JsResult?
                    ): Boolean {
                        result?.confirm()
                        return true
                    }

                    // Загрузка файлов с сайта
                    override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: ValueCallback<Array<Uri>>?,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {
                        if (filePathCallback == null) return false

                        activity.requestFilePick(filePathCallback, fileChooserParams)
                        return true
                    }
                }
            }
            viewModel.setWebView(wv)
            Log.d("WebView", "WebView created, loading URL: $url")
            wv.loadUrl(url)
            wv

        }, update = { wv ->
            // Если AndroidView пересоздал WebView — обновляем ссылку в ViewModel
            viewModel.setWebView(wv)
            if (wv.url != url && url != "about:blank") {
                Log.d("WebView", "URL changed, loading: $url")
                wv.loadUrl(url)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}