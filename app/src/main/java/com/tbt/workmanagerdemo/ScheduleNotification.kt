package com.tbt.workmanagerdemo

import android.content.Context

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleNotification(context: Context, delayInMillis: Long, title: String, message: String) {
    val data = Data.Builder()
        .putString("title", title)
        .putString("message", message)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}