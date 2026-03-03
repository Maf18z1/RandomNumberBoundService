package com.example.randomnumberboundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val serviceState = mutableStateOf<RandomNumberService?>(null)
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RandomNumberService.LocalBinder
            serviceState.value = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceState.value = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        service = serviceState.value,
                        onBindClick = { bindToService() },
                        onUnbindClick = { unbindFromService() }
                    )
                }
            }
        }
    }

    private fun bindToService() {
        if (!isBound) {
            val intent = Intent(this, RandomNumberService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindFromService() {
        if (isBound) {
            serviceState.value?.stopGenerating()
            unbindService(connection)
            isBound = false
            serviceState.value = null
        }
    }

    override fun onStop() {
        super.onStop()
        // Отвязываемся при скрытии активности, чтобы не держать сервис
        unbindFromService()
    }
}

@Composable
fun MainScreen(
    service: RandomNumberService?,
    onBindClick: () -> Unit,
    onUnbindClick: () -> Unit
) {
    var currentNumber by remember { mutableIntStateOf(0) }

    DisposableEffect(service) {
        val listener = object : RandomNumberService.OnNumberChangedListener {
            override fun onNumberChanged(newNumber: Int) {
                currentNumber = newNumber
            }
        }
        service?.setListener(listener)
        service?.startGenerating()
        onDispose {
            service?.setListener(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (service != null) currentNumber.toString() else "Not connected",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onBindClick,
            enabled = service == null
        ) {
            Text("Подключиться")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onUnbindClick,
            enabled = service != null
        ) {
            Text("Отключиться")
        }
    }
}