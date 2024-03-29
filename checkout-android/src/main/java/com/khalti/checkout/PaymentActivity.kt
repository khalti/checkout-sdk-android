// Copyright (c) 2022. The Khalti Authors. All rights reserved.

package com.khalti.checkout

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.khalti.checkout.payment.KhaltiPaymentPage
import com.khalti.checkout.payment.KhaltiPaymentViewModel
import com.khalti.checkout.payment.onBack
import com.khalti.checkout.resource.Url
import com.khalti.checkout.view.EPaymentWebClient

internal class PaymentActivity : ComponentActivity() {
    private var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KhaltiPaymentPage(this, KhaltiPaymentViewModel(), buildWebView())
        }
        registerBroadcast()
        setupBackPressListener()
    }

    override fun onDestroy() {
        unregisterBroadcast()
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

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerBroadcast() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null && intent.action.equals("close_khalti_payment_portal")) {
                    finish()
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    receiver, IntentFilter("close_khalti_payment_portal"), RECEIVER_NOT_EXPORTED
                )
            } else {
                registerReceiver(
                    receiver, IntentFilter("close_khalti_payment_portal"),
                )
            }
        }
    }

    private fun unregisterBroadcast() {
        unregisterReceiver(receiver)
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

