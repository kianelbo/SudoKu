package com.kian.sudoku

import android.os.Handler
import android.widget.TextView

class TimeHandler(private val timeView: TextView) : Runnable {
    private var startTime: Long = 0
    var elapsedTime: Long = 0
        private set
    private var savedMillis: Long
    private val timeHandler = Handler()

    companion object {
        fun formattedTime(t: Long): String {
            val seconds = t % 60
            val minutes = t / 60
            return String.format("%d:%02d", minutes, seconds)
        }
    }

    override fun run() {
        elapsedTime = savedMillis + System.currentTimeMillis() - startTime
        timeView.text = formattedTime(elapsedTime / 1000)
//        var seconds = (elapsedTime / 1000).toInt()
//        val minutes = seconds / 60
//        seconds %= 60
//        timeView.text = String.format("%d:%02d", minutes, seconds)
        timeHandler.postDelayed(this, 500)
    }

    fun setSavedMillis(savedMillis: Long) {
        this.savedMillis = savedMillis
    }

    init {
        startTime = System.currentTimeMillis()
        timeHandler.postDelayed(this, 0)
        savedMillis = 0
    }
}