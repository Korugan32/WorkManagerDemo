package com.tbt.workmanagerdemo

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Hatırlatma"
        val message = inputData.getString("message") ?: "Zamanı geldi!"

        showNotification(applicationContext, title, message)
        return Result.success()
    }
}
