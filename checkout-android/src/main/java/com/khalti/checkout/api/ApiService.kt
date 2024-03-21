/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.api

import com.khalti.checkout.data.PaymentPayload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("epayment/lookup/")
    suspend fun verify(@Body body: Map<String, String>): Response<PaymentPayload>
}