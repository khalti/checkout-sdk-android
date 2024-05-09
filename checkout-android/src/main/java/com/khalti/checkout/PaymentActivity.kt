// Copyright (c) 2022. The Khalti Authors. All rights reserved.

package com.khalti.checkout

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.khalti.checkout.payment.KhaltiPaymentPage
import com.khalti.checkout.payment.KhaltiPaymentViewModel
import com.khalti.checkout.payment.onBack
import com.khalti.checkout.signal.Connection
import com.khalti.checkout.signal.Signal
import com.khalti.checkout.signal.SignalKey

internal class PaymentActivity : ComponentActivity() {
    private var connection: Connection<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KhaltiPaymentPage(this, KhaltiPaymentViewModel(), buildWebView())
        }
        registerSignal()
        setupBackPressListener()
    }

    override fun onDestroy() {
        unregisterSignal()
        super.onDestroy()
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "@Suppress(\"DEPRECATION\") super.onBackPressed()", "android.app.Activity"
        )
    )
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onBack()
        }
        @Suppress("DEPRECATION") super.onBackPressed()
    }

    private fun registerSignal() {
        Signal.instance().connect<String>(SignalKey.ClosePayment) {
            finish()
        }
    }

    private fun unregisterSignal() {
        Signal.instance().disconnect(connection)
    }

    private fun setupBackPressListener() {
        if (Build.VERSION.SDK_INT >= 33) {
            val priority = OnBackInvokedDispatcher.PRIORITY_DEFAULT
            onBackInvokedDispatcher.registerOnBackInvokedCallback(priority) {
                onBack()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun buildWebView(): WebView {
        return WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(true)
            this.clearCache(true)
        }
    }
}

