package com.banasiak.android.muzeisaver.quicksettings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.banasiak.android.muzeisaver.download.DownloadActivity
import com.banasiak.android.muzeisaver.util.toggleLauncherIcon

@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingsService : TileService() {
  companion object {
    private const val DIALOG_SHOWN_KEY = "dialog_shown"
  }

  private val prefs: SharedPreferences by lazy { getSharedPreferences(packageName, Context.MODE_PRIVATE) }

  override fun onClick() {
    if (isLocked) {
      return
    }

    if (prefs.getBoolean(DIALOG_SHOWN_KEY, false)) {
      startActivityAndCollapse(
        Intent(this, DownloadActivity::class.java).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
      )
      return
    }

    startActivityAndCollapse(
      Intent(this, ToggleLauncherActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
      }
    )
    prefs.edit { putBoolean(DIALOG_SHOWN_KEY, true) }
  }

  override fun onTileRemoved() {
    super.onTileRemoved()
    application.toggleLauncherIcon(enable = true)
    prefs.edit { clear() }
  }
}