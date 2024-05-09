/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.service.verification

import com.khalti.checkout.Khalti
import com.khalti.checkout.data.PaymentResult
import com.khalti.checkout.resource.KFailure
import com.khalti.checkout.resource.OnMessageEvent
import com.khalti.checkout.resource.OnMessagePayload
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VerificationRepository {
    private val verificationService: VerificationService by lazy {
        VerificationService()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun verify(pidx: String, khalti: Khalti, onComplete: (() -> Unit)? = null) {
        GlobalScope.launch {
            val result = verificationService.verify(pidx)
            onComplete?.invoke()
            result.match(
                ok = {
                    khalti.onPaymentResult.invoke(
                        PaymentResult(
                            status = it.status ?: "Payment successful", payload = it
                        ), khalti
                    )
                },
                err = {
                    val messageEvent = when (it) {
                        is KFailure.NoNetwork, is KFailure.ServerUnreachable -> OnMessageEvent.NetworkFailure
                        is KFailure.HttpCall, is KFailure.Payment -> OnMessageEvent.PaymentLookUpFailure
                        else -> OnMessageEvent.Unknown

                    }
                    val needsConfirmations = when (it) {
                        is KFailure.NoNetwork, is KFailure.ServerUnreachable, is KFailure.Generic -> true
                        else -> false
                    }
                    khalti.onMessage.invoke(
                        OnMessagePayload(
                            messageEvent,
                            it.message ?: "",
                            it.cause,
                            it.code,
                            needsPaymentConfirmation = needsConfirmations
                        ),
                        khalti,
                    )
                },
            )
        }
    }
}