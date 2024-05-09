/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.data

import com.google.gson.annotations.SerializedName

data class PaymentResult(
    val status: String,
    val payload: PaymentPayload? = null,
    val message: String? = null
)

data class PaymentPayload(
    @SerializedName("pidx") val pidx: String?,
    @SerializedName("total_amount") val totalAmount: Long = 0,
    @SerializedName("status") val status: String?,
    @SerializedName("transaction_id") val transactionId: String?,
    @SerializedName("fee") val fee: Long = 0,
    @SerializedName("refunded") val refunded: Boolean = false,
    @SerializedName("purchase_order_id") val purchaseOrderId: String?,
    @SerializedName("purchase_order_name") val purchaseOrderName: String?,
    @SerializedName("extra_merchant_params") val extraMerchantParams: Map<String, Any>?,
) {
    override fun toString(): String {
        return StringBuilder()
            .append("pidx: $pidx\n")
            .append("totalAmount: $totalAmount\n")
            .append("status: $status\n")
            .append("transactionId: $transactionId\n")
            .append("fee: $fee\n")
            .append("refunded: $refunded\n")
            .append("purchase_order_id: $purchaseOrderId\n")
            .append("purchase_order_name: $purchaseOrderName\n")
            .append("extra_merchant_params: $extraMerchantParams\n")
            .toString()
    }
}