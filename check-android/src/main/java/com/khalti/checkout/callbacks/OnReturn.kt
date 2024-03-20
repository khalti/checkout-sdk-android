/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.callbacks

import com.khalti.checkout.Khalti

fun interface OnReturn {
    fun invoke(khalti: Khalti)
}