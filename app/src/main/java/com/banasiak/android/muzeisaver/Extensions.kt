package com.banasiak.android.muzeisaver

import com.google.android.apps.muzei.api.Artwork
import java.util.Date

fun Artwork.generateFilename(): String {

  val title: String? = this.title.capitalizeWords()
  val byline: String? = this.byline.capitalizeWords()
  val date = Date().toString()

  var filename = title?.trim() ?: ""

  if (byline != null) {
    filename += if (filename.isEmpty()) {
      byline.trim()
    } else {
      " by ${byline.trim()}"
    }
  }

  filename += if (filename.isEmpty()) {
    "$date.png"
  } else {
    " ($date).png"
  }

  return filename

}

fun String.capitalizeWords(): String {
  return this.split(" ").joinToString(" ") { it.capitalize() }
}
