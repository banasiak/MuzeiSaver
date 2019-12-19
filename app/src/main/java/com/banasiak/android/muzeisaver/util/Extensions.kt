package com.banasiak.android.muzeisaver.util

import android.annotation.SuppressLint
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
