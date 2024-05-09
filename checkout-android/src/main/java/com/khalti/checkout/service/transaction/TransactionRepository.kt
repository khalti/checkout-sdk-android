/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.service.transaction

import com.khalti.checkout.data.PaymentDetailModel
import com.khalti.checkout.resource.KFailure
import com.khalti.checkout.resource.Result

class TransactionRepository {
    private val transactionService: TransactionService by lazy {
        TransactionService()
    }

    suspend fun fetchDetail(pidx: String): Result<PaymentDetailModel, KFailure> {
        return transactionService.fetchDetail(pidx)
    }
}