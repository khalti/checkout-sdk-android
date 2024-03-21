/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.resource

import com.khalti.checkout.Khalti

data class OnMessagePayload(
    val event: OnMessageEvent,
    val message: String,
    val throwable: Throwable? = null,
    val code: Number? = null,
    val needsPaymentConfirmation: Boolean = false,
)
