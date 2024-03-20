/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.callbacks

import com.khalti.checkout.Khalti
import com.khalti.checkout.data.PaymentResult

fun interface OnPaymentResult {
    fun invoke(result: PaymentResult, khalti: Khalti)
}