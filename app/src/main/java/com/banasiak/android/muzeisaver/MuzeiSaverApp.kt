package com.banasiak.android.muzeisaver

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import androidx.core.app.NotificationManagerCompat
import com.banasiak.android.muzeisaver.download.DownloadService
import timber.log.Timber

class MuzeiSaverApp : Application() {

  private val isAndroidM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
  private val isAndroidO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

  @SuppressLint("NewApi")
  override fun onCreate() {
    super.onCreate()

    if (isAndroidO) {
      val notificationManager = NotificationManagerCompat.from(this)
      val notificationChannel = NotificationChannel(
        DownloadService.NOTIFICATION_ID.toString(),
        getString(R.string.download_service),
        NotificationManager.IMPORTANCE_LOW)
      notificationManager.createNotificationChannels(listOf(notificationChannel))
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
        .detectNetwork()
        .detectResourceMismatches()
        .detectUnbufferedIo()
        .also {
          if (isAndroidM) it.detectResourceMismatches()
          if (isAndroidO) it.detectUnbufferedIo()
        }
        .penaltyLog()
        .penaltyDeath()
        .build())

      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build())
    }
  }
}