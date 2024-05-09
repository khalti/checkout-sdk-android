package com.khalti.checkout.signal

class Connection<T>(
    val id: SignalKey,
    val callBack: (T) -> Unit,
    private val signal: Signal
) {
    fun disconnect() {
        signal.disconnect(this)
    }
}