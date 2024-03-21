/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.service

import com.khalti.checkout.api.ApiClient
import com.khalti.checkout.api.ApiService
import com.khalti.checkout.api.safeApiCall
import com.khalti.checkout.resource.KFailure
import com.khalti.checkout.resource.Result
import com.khalti.checkout.data.PaymentPayload
import kotlinx.coroutines.Dispatchers

class VerificationService {
    private val apiService: ApiService by lazy {
        ApiClient.build()
    }

    suspend fun verify(pidx: String): Result<PaymentPayload, KFailure> {
        return safeApiCall(Dispatchers.IO) {
            apiService.verify(mapOf("pidx" to pidx))
        }
    }
}