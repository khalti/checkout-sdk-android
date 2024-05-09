/*
 * Copyright (c) 2024. The Khalti Authors. All rights reserved.
 */

package com.khalti.checkout.resource

abstract class KFailure(
    failureMessage: String,
    throwable: Throwable? = null,
    val code: Number? = null,
    val failureMap: Map<String, String>? = null,
) :
    Exception(failureMessage, throwable) {

    class NoNetwork(
        message: String,
        cause: Throwable? = null,
        failureMap: Map<String, String>? = null,
    ) : KFailure(message, cause, failureMap = failureMap)

    class ServerUnreachable(
        message: String, cause: Throwable? = null,
        failureMap: Map<String, String>? = null,
    ) : KFailure(message, cause, failureMap = failureMap)

    class HttpCall(
        message: String,
        cause: Throwable? = null,
        code: Number?,
        failureMap: Map<String, String>? = null
    ) :
        KFailure(message, cause, code, failureMap = failureMap)

    class Payment(
        message: String,
        cause: Throwable? = null,
        code: Number?,
        failureMap: Map<String, String>? = null
    ) :
        KFailure(message, cause, code, failureMap = failureMap)

    class Generic(
        message: String,
        cause: Throwable? = null,
        failureMap: Map<String, String>? = null,
    ) : KFailure(message, cause, failureMap = failureMap)
}