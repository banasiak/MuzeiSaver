package com.banasiak.android.muzeisaver

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import androidx.core.app.NotificationManagerCompat
import com.banasiak.android.muzeisaver.download.DownloadService
import timber.log.Timber

class MuzeiSaverApp : Application() {
  override fun onCreate() {
    super.onCreate()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = NotificationManagerCompat.from(this)
      val notificationChannel = NotificationChannel(
        DownloadService.DOWNLOAD_COMPLETE_ID.toString(),
        getString(R.string.download_complete),
        NotificationManager.IMPORTANCE_HIGH)
      notificationManager.createNotificationChannels(listOf(notificationChannel))
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
        .detectNetwork()
        .apply {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) detectResourceMismatches()
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) detectUnbufferedIo()
        }
        .penaltyLog()
        .build())

      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build())
    }
  }
}