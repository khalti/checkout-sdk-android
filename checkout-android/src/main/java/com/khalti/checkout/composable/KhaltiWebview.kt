/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.composable

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.khalti.checkout.data.KhaltiPayConfig
import com.khalti.checkout.resource.Strings
import com.khalti.checkout.resource.Url
import com.khalti.checkout.view.EPaymentWebClient

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KhaltiWebView(
    config: KhaltiPayConfig,
    onReturnPageLoaded: () -> Unit,
    onPageLoaded: () -> Unit,
    androidWebView: WebView,
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { _ ->
            androidWebView.webViewClient = EPaymentWebClient(onReturnPageLoaded)
            androidWebView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100) {
                        onPageLoaded()
                    }
                }
            }
            val baseUrl = if (config.isProd()) {
                Url.BASE_PAYMENT_URL_PROD
            } else {
                Url.BASE_PAYMENT_URL_STAGING
            }

            val paymentUri =
                Uri.parse(baseUrl.value).buildUpon().appendQueryParameter("pidx", config.pidx)

            androidWebView.loadUrl(paymentUri.toString())

            return@AndroidView androidWebView
        },
        update = {
            it.loadUrl(Strings.RELOAD_URL)
        }
    )
}
