/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KhaltiPayConfig(
    val publicKey: String,
    val pidx: String,
    val openInKhalti: Boolean = true,
    val environment: Environment = Environment.PROD,
) : Parcelable {

    fun isProd(): Boolean = environment == Environment.PROD
}
