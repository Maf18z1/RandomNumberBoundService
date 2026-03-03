package com.example.randomnumberboundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class RandomNumberService : Service() {

    private val binder = LocalBinder()
    private var generationJob: Job? = null
    private var currentNumber = 0
    private var listener: OnNumberChangedListener? = null

    inner class LocalBinder : Binder() {
        fun getService(): RandomNumberService = this@RandomNumberService
    }

    interface OnNumberChangedListener {
        fun onNumberChanged(newNumber: Int)
    }

    fun setListener(listener: OnNumberChangedListener?) {
        this.listener = listener
    }

    fun startGenerating() {
        if (generationJob != null) return
        generationJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000L)
                currentNumber = Random.nextInt(0, 101)
                withContext(Dispatchers.Main) {
                    listener?.onNumberChanged(currentNumber)
                }
            }
        }
    }

    fun stopGenerating() {
        generationJob?.cancel()
        generationJob = null
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        stopGenerating()
        super.onDestroy()
    }
}