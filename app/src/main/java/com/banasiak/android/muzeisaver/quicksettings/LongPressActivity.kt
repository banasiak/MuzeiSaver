package com.banasiak.android.muzeisaver.quicksettings

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.banasiak.android.muzeisaver.LauncherActivity
import com.banasiak.android.muzeisaver.R
import timber.log.Timber

class LongPressActivity : AppCompatActivity(), QuickSettingsDialog.DialogListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val dialog = QuickSettingsDialog()
    dialog.initialize(this, this)
    dialog.show(supportFragmentManager, "QuickSettingsDialog")

  }

  override fun onPositiveClick() {
    Timber.i("Enabling launcher icon")
    toggleLauncherIcon(enable = true)
    finish()
  }

  override fun onNegativeClick() {
    Timber.i("Disabling launcher icon")
    toggleLauncherIcon(enable = false)
    finish()
  }

  private fun toggleLauncherIcon(enable: Boolean) {
    val state: Int
    val message: Int
    if (enable) {
      state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED
      message = R.string.launcher_enabled
    } else {
      state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED
      message = R.string.launcher_disabled
    }

    val component = ComponentName(this, LauncherActivity::class.java)
    packageManager.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP)
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}