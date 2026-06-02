/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.khalti.checkout.Khalti
import com.khalti.checkout.cache.Store
import com.khalti.checkout.composable.KProgressDialog
import com.khalti.checkout.composable.KhaltiError
import com.khalti.checkout.composable.KhaltiWebView
import com.khalti.checkout.resource.ErrorType
import com.khalti.checkout.resource.OnMessageEvent
import com.khalti.checkout.resource.OnMessagePayload
import com.khalti.checkout.resource.Strings
import com.khalti.checkout.utils.NetworkUtil

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun KhaltiPaymentPage(
    activity: Activity,
    viewModel: KhaltiPaymentViewModel,
    androidWebView: WebView,
) {
    Scaffold(
        topBar = {
            PaymentAppBar(activity = activity, androidWebView = androidWebView)
        },
    ) {
        PaymentBody(
            viewModel = viewModel,
            paddingValues = it,
            activity = activity,
            androidWebView = androidWebView
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentAppBar(
    activity: Activity,
    androidWebView: WebView,
) {
    Surface(shadowElevation = 4.dp) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    onBack()
                    activity.finish()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            title = {
                Text(text = "Payment Gateway")
            },
            actions = {
                IconButton(onClick = {
                    androidWebView.loadUrl(Strings.RELOAD_URL)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh, contentDescription = "Refresh"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFE8F0F7)
            )
        )
    }
}

@Composable
private fun PaymentBody(
    viewModel: KhaltiPaymentViewModel,
    paddingValues: PaddingValues,
    activity: Activity,
    androidWebView: WebView,
) {
    val state by viewModel.state.collectAsState()
    var verificationTriggerByReturnUrl = false

    LaunchedEffect(state.hasNetwork) {
        NetworkUtil.registerListener(activity) {
            viewModel.toggleNetwork(it)
        }
    }

    Surface(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
        val khalti = Store.instance().get<Khalti>("khalti")
        if (khalti != null) {
            if (state.progressDialog) {
                KProgressDialog()
            }

            val config = khalti.config

            if (state.hasNetwork) {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    KhaltiWebView(
                        config = config,
                        onReturnPageLoaded = {
                            if (!verificationTriggerByReturnUrl) {
                                viewModel.verifyPaymentStatus(khalti)
                                verificationTriggerByReturnUrl = true
                            }
                        },
                        onPageLoaded = {
                            viewModel.toggleLoading(false)
                        },
                        androidWebView = androidWebView,
                        returnUrl = getUrlData(khalti.config.paymentUrl, "return_url"),
                        mode = getUrlData(khalti.config.paymentUrl, "mode")
                    )
                }
            }


        } else {
            KhaltiError(errorType = ErrorType.network) {
                viewModel.toggleNetwork(NetworkUtil.isNetworkAvailable(activity))
            }
        }
    }
}

fun onBack() {
    val khalti = Store.instance().get<Khalti>("khalti")
    khalti?.onMessage?.invoke(
        OnMessagePayload(
            OnMessageEvent.KPGDisposed,
            "Khalti payment page disposed",
            needsPaymentConfirmation = true
        ),
        khalti,
    )
}

fun getUrlData(fullUrl: String, key: String): String {
    return fullUrl.toUri()
        .getQueryParameter(key) ?: ""
}