/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.service.transaction

import com.khalti.checkout.api.ApiClient
import com.khalti.checkout.api.ApiService
import com.khalti.checkout.api.safeApiCall
import com.khalti.checkout.data.PaymentDetailModel
import com.khalti.checkout.resource.KFailure
import com.khalti.checkout.resource.Result
import com.khalti.checkout.data.PaymentPayload
import kotlinx.coroutines.Dispatchers

class TransactionService {
    private val apiService: ApiService by lazy {
        ApiClient.build()
    }

    suspend fun fetchDetail(pidx: String): Result<PaymentDetailModel, KFailure> {
        return safeApiCall(Dispatchers.IO) {
            apiService.fetchDetail(mapOf("pidx" to pidx))
        }
    }
}