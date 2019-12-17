package com.banasiak.android.muzeisaver

import android.app.Application
import android.os.Build
import android.os.StrictMode
import timber.log.Timber

class DownloadApp : Application() {

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())

      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
        .detectNetwork()
        .penaltyLog()
        .penaltyDeath()
        .also {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            it.detectResourceMismatches()
          }
        }
        .build())

      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
        .detectLeakedClosableObjects()
        .detectLeakedRegistrationObjects()
        .detectLeakedSqlLiteObjects()
        .penaltyLog()
        .penaltyDeath()
        .build())
    }

  }
}