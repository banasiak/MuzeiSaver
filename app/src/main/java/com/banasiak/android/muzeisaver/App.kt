package com.banasiak.android.muzeisaver

import android.app.Application
import android.os.StrictMode
import timber.log.Timber

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())

      Timber.w("Strict mode enabled! Penalty death. Beware of unexplained crashes (and fix them)!")
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
        .detectNetwork()
        .detectResourceMismatches()
        .penaltyLog()
        .penaltyDeath()
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