/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.resource

enum class OnMessageEvent {
    KPGDisposed, ReturnUrlLoadFailure, NetworkFailure, PaymentLookUpFailure, Unknown, TransactionDetailLookUpFailure,
}