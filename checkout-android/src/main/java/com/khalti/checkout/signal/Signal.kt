package com.khalti.checkout.signal

import com.khalti.checkout.cache.Store

class Signal private constructor() {

    companion object {
        @Volatile
        private var instance: Signal? = null

        fun instance(): Signal {
            return instance ?: synchronized(this) {
                instance ?: Signal().also { instance = it }
            }
        }
    }

    private val callBackMap = HashMap<SignalKey, Any>()

    fun <T> connect(key: SignalKey, callBack: (T) -> Unit): Connection<T> {
        val connection = Connection(key, callBack, this)
        callBackMap[connection.id] = connection
        return connection
    }

    fun <T> disconnect(connection: Connection<T>?) {
        if (connection != null) {
            callBackMap.remove(connection.id)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> dispatch(key: SignalKey, payload: T) {
        val connection = callBackMap[key] as Connection<T>?
        connection?.callBack?.invoke(payload)
    }
}