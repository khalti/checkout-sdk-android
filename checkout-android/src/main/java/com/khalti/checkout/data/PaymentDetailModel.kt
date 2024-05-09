package com.khalti.checkout.data

import com.google.gson.annotations.SerializedName

data class PaymentDetailModel(
    @SerializedName("return_url") val returnUrl: String?,
) {
    override fun toString(): String {
        return StringBuilder()
            .append("return_url: $returnUrl\n")
            .toString()
    }
}
