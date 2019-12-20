package com.banasiak.android.muzeisaver.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.widget.Toast
import com.banasiak.android.muzeisaver.LauncherActivity
import com.banasiak.android.muzeisaver.R
import com.google.android.apps.muzei.api.Artwork
import java.util.Date

fun Artwork.generateFilename(): String {
  val title: String? = this.title?.capitalizeWords()
  val byline: String? = this.byline?.capitalizeWords()
  var filename = title?.trim() ?: ""
  if (byline != null) {
    filename += if (filename.isEmpty()) byline.trim() else " [${byline.trim()}]"
  }
  filename += if (filename.isEmpty()) "${Date()}.png" else ".png"
  return filename
}

@SuppressLint("DefaultLocale")
fun String.capitalizeWords(): String {
  return this.split(" ").joinToString(" ") { it.capitalize() }
}

fun Application.toggleLauncherIcon(enable: Boolean) {
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
