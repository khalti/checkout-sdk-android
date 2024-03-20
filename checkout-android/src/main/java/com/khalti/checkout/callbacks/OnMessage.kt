/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.callbacks

import com.khalti.checkout.Khalti
import com.khalti.checkout.resource.OnMessagePayload

fun interface OnMessage {
    fun invoke(payload: OnMessagePayload, khalti: Khalti)
}