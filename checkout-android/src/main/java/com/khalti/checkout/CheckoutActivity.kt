package com.khalti.checkout

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.khalti.checkout.cache.Store
import com.khalti.checkout.payment.KhaltiPaymentViewModel
import com.khalti.checkout.resource.OnMessageEvent
import com.khalti.checkout.resource.OnMessagePayload


// Package (applicationId) of the installed Khalti app to launch for payment.
// Production: "com.khalti"  |  Develop/test build: "com.khalti.AS.red"
const val KHALTI_APP_PACKAGE = "com.khalti.AS.red"

// The launched Activity's class name does NOT change with an applicationIdSuffix.
const val KHALTI_APP_ACTIVITY = "com.khalti.MainActivity"

enum class KhaltiStatus { COMPLETED, SETTLED, EXPIRED, ERROR, CANCELLED }

data class KhaltiResult(val status: KhaltiStatus, val message: String?)

val khalti: Khalti? get() = Store.instance().get<Khalti>("khalti")

fun parseKhaltiResult(data: Intent?): KhaltiResult {
    val message = data?.getStringExtra("reply")
    val status = when (data?.getStringExtra("status")) {
        "completed" -> KhaltiStatus.COMPLETED
        "settled" -> KhaltiStatus.SETTLED
        "expired" -> KhaltiStatus.EXPIRED
        "error" -> KhaltiStatus.ERROR
        "cancelled" -> KhaltiStatus.CANCELLED
        else -> return KhaltiResult(KhaltiStatus.ERROR, "Result without a valid status")
    }
    return KhaltiResult(status, message)
}

var khaltiPaymentViewModel: KhaltiPaymentViewModel = KhaltiPaymentViewModel()

class CheckoutActivity : ComponentActivity() {

    private val khaltiLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onKhaltiResult(result = result)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pidx = khalti?.config?.pidx
        payWithKhalti(pidx, this)
    }

    private var pendingPidx: String? = null

    fun payWithKhalti(pidx: String?, context: Context) {
        pendingPidx = pidx
        if (pidx.isNullOrEmpty()) return

        val link = Uri.Builder()
            .scheme("https").authority("khalti.com").path("/go/")
            .appendQueryParameter("t", "intent-pay")
            .appendQueryParameter("pidx", pidx)
            .build()

        val intent = Intent(Intent.ACTION_VIEW, link).apply {
            setClassName(KHALTI_APP_PACKAGE, KHALTI_APP_ACTIVITY)
        }
        khaltiLauncher.launch(intent)
    }


    private fun onKhaltiResult(result: ActivityResult) {
        val khaltiResult = parseKhaltiResult(result.data)
        when (khaltiResult.status) {
            KhaltiStatus.COMPLETED, KhaltiStatus.SETTLED -> {
                khaltiPaymentViewModel.verifyPaymentStatus(khalti = khalti!!)
                returnToPreviousPage(RESULT_OK, khaltiResult.message)
            }

            KhaltiStatus.EXPIRED -> notifyAndReturn(
                OnMessageEvent.KPGDisposed,
                khaltiResult.message ?: "Khalti payment link has expired",
            )

            KhaltiStatus.ERROR -> notifyAndReturn(
                OnMessageEvent.Unknown,
                khaltiResult.message ?: "Something went wrong during payment",
            )

            KhaltiStatus.CANCELLED -> notifyAndReturn(
                OnMessageEvent.Unknown,
                khaltiResult.message ?: "Payment was cancelled",
            )
        }
    }

    private fun notifyAndReturn(event: OnMessageEvent, message: String) {
        val khalti = Store.instance().get<Khalti>("khalti")
        khalti?.onMessage?.invoke(
            OnMessagePayload(event, message, needsPaymentConfirmation = true),
            khalti,
        )
        returnToPreviousPage(RESULT_CANCELED, message)
    }

    private fun returnToPreviousPage(resultCode: Int, message: String?) {
        setResult(resultCode, Intent().putExtra("reply", message))
        finish()
    }
}