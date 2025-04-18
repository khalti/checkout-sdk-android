// Copyright (c) 2022. The Khalti Authors. All rights reserved.

package com.khalti.checkout.demo.composable

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khalti.android.demo.R
import com.khalti.checkout.Khalti
import com.khalti.checkout.data.Environment
import com.khalti.checkout.data.KhaltiPayConfig
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview
fun DemoScreen() {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val khalti = Khalti.init(
        LocalContext.current,
        KhaltiPayConfig(
            publicKey = "live_public_key_979320ffda734d8e9f7758ac39ec775f",
            pidx = "L3A7eG4mEZLvRL8oeGwNfL",
            environment = Environment.TEST
        ),
        onPaymentResult = { paymentResult, khalti ->
            Log.i("Demo | onPaymentResult", paymentResult.toString())
            khalti.close()
            scope.launch {
                snackBarHostState.showSnackbar("Payment successful for pidx: ${khalti.config.pidx}")
            }
        },
        onMessage = { payload, khalti ->
            Log.i(
                "Demo | onMessage",
                "${payload.event} ${if (payload.code != null) "(${payload.code})" else ""} | ${payload.message} | ${payload.needsPaymentConfirmation}"
            )
            payload.throwable?.printStackTrace()
            khalti.close()
            scope.launch {
                snackBarHostState.showSnackbar("OnMessage: ${payload.message}")
            }
        },
        onReturn = { _ ->
            Log.i("Demo | onReturn", "OnReturn")
        }
    )

    val publicKey = remember {
        mutableStateOf(khalti.config.publicKey)
    }
    val pidx = remember {
        mutableStateOf(khalti.config.pidx)
    }
    val environments = enumValues<Environment>()
    val selectedEnvironment = remember {
        mutableStateOf(khalti.config.environment)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        content = {
            Column(
                Modifier
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.padding(16.dp))
                Image(
                    painterResource(R.mipmap.seru),
                    contentDescription = "Khalti Logo",
                    modifier = Modifier.height(180.dp)
                )
                Spacer(Modifier.height(30.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = publicKey.value,
                    onValueChange = {
                        publicKey.value = it
                    },
                    label = { Text(text = "Public Key") },
                )
                Spacer(Modifier.height(20.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = pidx.value,
                    onValueChange = {
                        pidx.value = it
                    },
                    label = { Text(text = "PIDX") },
                )
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = "Environment")
                    environments.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (it == selectedEnvironment.value),
                                onClick = { selectedEnvironment.value = it }
                            )
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(30.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "Rs. 22", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "1 day fee", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        {
                            if (publicKey.value != khalti.config.publicKey
                                || pidx.value != khalti.config.pidx
                                || selectedEnvironment.value != khalti.config.environment
                            ) {
                                khalti.config = khalti.config.copy(
                                    publicKey = publicKey.value,
                                    pidx = pidx.value,
                                    environment = selectedEnvironment.value,
                                )
                            }

                            khalti.open()
                        }
                    ) {
                        Text("Pay Rs. 22")
                    }
                }
                Spacer(Modifier.height(30.dp))
                Text(
                    text = "This is a demo application developed by some merchant.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
    )
}
