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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KhaltiPaymentState(
    val isLoading: Boolean = true,
    val hasNetwork: Boolean = true,
    val progressDialog: Boolean = false,
    val returnUrl: String? = null,
    val loadWebView: Boolean = false,
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
                            state.copy(returnUrl = returnUrl)
                        }
                    }
                },
                err = {
                    /*no-op*/
                },
            )
            _state.update { it.copy(loadWebView = true) }
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