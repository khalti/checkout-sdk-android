/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.view

import android.net.Uri
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.khalti.checkout.Khalti
import com.khalti.checkout.cache.Store
import com.khalti.checkout.resource.OnMessageEvent
import com.khalti.checkout.resource.OnMessagePayload

internal class EPaymentWebClient(
    private val returnUrl: String?,
    private val paymentUrl: String,
    val onReturn: () -> Unit
) :
    WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?):
            Boolean = handleUri(request!!.url)

    @SuppressWarnings("deprecation")
    @Deprecated("")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?):
            Boolean = handleUri(Uri.parse(url))

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) = handleError(request?.url.toString(), error?.description.toString())

    @SuppressWarnings("deprecation")
    @Deprecated("")
    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) = handleError(failingUrl, description)

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val resolvedUrl = url ?: ""

        val isPaymentPage = resolvedUrl == paymentUrl
        val isReturnPage = returnUrl == null || resolvedUrl.startsWith(returnUrl)
        if (!isPaymentPage && isReturnPage) {
            invokeOnReturn()
        }
    }

    private fun handleUri(uri: Uri): Boolean {
        // TODO (Ishwor) Handle redirection to Khalti app for setting MPIN
        val url = uri.toString()
        // MPIN url : /account/transaction_pin
        return false
    }

    private fun handleError(failingUrl: String?, description: String?) {
        val khalti = Store.instance().get<Khalti>("khalti")
        if (khalti != null) {
            if (description != null) {
                if (failingUrl?.startsWith(returnUrl ?: "") != false) {
                    khalti.onMessage.invoke(
                        OnMessagePayload(
                            OnMessageEvent.ReturnUrlLoadFailure,
                            description,
                            needsPaymentConfirmation = true
                        ),
                        khalti,
                    )
                }
            }
        }
    }

    private fun invokeOnReturn() {
        val khalti = Store.instance().get<Khalti>("khalti")
        khalti?.onReturn?.invoke(khalti)
        onReturn()
    }
}
