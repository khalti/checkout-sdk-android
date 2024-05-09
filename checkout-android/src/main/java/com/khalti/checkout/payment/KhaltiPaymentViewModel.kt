/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.payment

import androidx.lifecycle.ViewModel
import com.khalti.checkout.Khalti
import com.khalti.checkout.service.transaction.TransactionRepository
import com.khalti.checkout.service.verification.VerificationRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

data class KhaltiPaymentState(
    val isLoading: Boolean = true,
    val hasNetwork: Boolean = false,
    val progressDialog: Boolean = false,
    val loadWebView: Boolean = false,
    val returnUrl: String? = null,
    val error: String? = null,
)

class KhaltiPaymentViewModel : ViewModel() {
    private val _state = MutableStateFlow((KhaltiPaymentState()))
    private val verificationRepo = VerificationRepository()
    private val transactionRepo = TransactionRepository()
    val state: StateFlow<KhaltiPaymentState> = _state

    fun verifyPaymentStatus(khalti: Khalti) {
        toggleProgressDialog()
        verificationRepo.verify(khalti.config.pidx, khalti) {
            toggleProgressDialog(false)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchDetail(khalti: Khalti) {
        GlobalScope.launch {
            val result = transactionRepo.fetchDetail(khalti.config.pidx)
            result.match(
                ok = {
                    val returnUrl = it.returnUrl
                    if (returnUrl != null) {
                        _state.update { state ->
                            state.copy(returnUrl = returnUrl, error = "", loadWebView = true)
                        }
                    }
                },
                err = { failure ->
                    _state.update {
                        it.copy(
                            error = failure.failureMap?.get("detail")
                                ?: "There was an error setting up your payment. Please try again later.",
                            loadWebView = false,
                            isLoading = false
                        )
                    }
                },
            )
        }
    }

    fun toggleNetwork(hasNetwork: Boolean) {
        _state.update { it.copy(hasNetwork = hasNetwork) }
    }

    fun toggleLoading(show: Boolean = true) {
        _state.update { it.copy(isLoading = show) }
    }

    private fun toggleProgressDialog(show: Boolean = true) {
        _state.update { it.copy(progressDialog = show) }
    }
}