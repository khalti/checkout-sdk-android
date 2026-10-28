/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
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
    }

    fun open() {
        val packageName = context.packageName
        val store = Store.instance()
        val packageInfo = PackageUtil.getPackageInfo(context, packageName)

        store.put("merchant_package_name", packageName)
        store.put("merchant_package_version", packageInfo?.versionName ?: "")

        if (isPackageInstalled(KHALTI_APP_PACKAGE)) {
            context.startActivity(Intent(context, CheckoutActivity::class.java))
        } else {
            context.startActivity(Intent(context, PaymentActivity::class.java))

        }

    }


    private val packageManager = context.applicationContext.packageManager
    fun isPackageInstalled(pkg: String): Boolean = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                pkg,
                PackageManager.PackageInfoFlags.of(0L)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(pkg, 0)
        }
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }



    fun close() {
        Signal.instance().dispatch(SignalKey.ClosePayment, "Close")
    }
}