/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout

import android.content.Context
import android.content.Intent
import com.khalti.checkout.service.verification.VerificationRepository
import com.khalti.checkout.data.KhaltiPayConfig
import com.khalti.checkout.callbacks.OnMessage
import com.khalti.checkout.callbacks.OnPaymentResult
import com.khalti.checkout.callbacks.OnReturn
import com.khalti.checkout.cache.Store
import com.khalti.checkout.signal.Signal
import com.khalti.checkout.signal.SignalKey
import com.khalti.checkout.utils.PackageUtil

// Though kotlin provides named and optional parameters
// method overloading was required for Java developers
class Khalti private constructor(
    private val context: Context,
    var config: KhaltiPayConfig,
    val onPaymentResult: OnPaymentResult,
    val onMessage: OnMessage,
    val onReturn: OnReturn?,
) {
    companion object {
        fun init(
            context: Context,
            config: KhaltiPayConfig,
            onPaymentResult: OnPaymentResult,
            onMessage: OnMessage,
            onReturn: OnReturn,
        ): Khalti {
            val khalti = Khalti(
                context,
                config,
                onPaymentResult,
                onMessage,
                onReturn,
            )

            Store.instance().put("khalti", khalti)
            return khalti
        }

        fun init(
            context: Context,
            config: KhaltiPayConfig,
            onPaymentResult: OnPaymentResult,
            onMessage: OnMessage,
        ): Khalti {
            val khalti = Khalti(
                context,
                config,
                onPaymentResult,
                onMessage,
                null,
            )

            Store.instance().put("khalti", khalti)

            return khalti
        }
    }

    fun open() {
        val packageName = context.packageName
        val store = Store.instance()
        val packageInfo = PackageUtil.getPackageInfo(context, packageName)

        store.put("merchant_package_name", packageName)
        store.put("merchant_package_version", packageInfo?.versionName ?: "")

        val intent = Intent(context, PaymentActivity::class.java)
        context.startActivity(intent)
    }

    fun verify() {
        val verificationRepo = VerificationRepository()
        verificationRepo.verify(config.pidx, this)
    }

    fun close() {
        Signal.instance().dispatch(SignalKey.ClosePayment, "Close")
    }
}