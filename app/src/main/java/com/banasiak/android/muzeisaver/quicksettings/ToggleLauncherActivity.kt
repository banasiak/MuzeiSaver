package com.banasiak.android.muzeisaver.quicksettings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.banasiak.android.muzeisaver.util.toggleLauncherIcon
import timber.log.Timber

class ToggleLauncherActivity : AppCompatActivity(), QuickSettingsDialog.DialogListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val dialog = QuickSettingsDialog()
    dialog.initialize(this, this)
    dialog.show(supportFragmentManager, "QuickSettingsDialog")
  }

  override fun onPositiveClick() {
    Timber.i("Enabling launcher icon")
    application.toggleLauncherIcon(enable = true)
    finish()
  }

  override fun onNegativeClick() {
    Timber.i("Disabling launcher icon")
    application.toggleLauncherIcon(enable = false)
    finish()
  }
}