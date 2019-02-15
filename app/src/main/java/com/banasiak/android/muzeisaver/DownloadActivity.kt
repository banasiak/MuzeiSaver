package com.banasiak.android.muzeisaver

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.apps.muzei.api.Artwork
import com.google.android.apps.muzei.api.MuzeiContract
import java.io.File
import java.io.FileNotFoundException
import java.util.Date

class DownloadActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "DownloadActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val artwork: Artwork? = MuzeiContract.Artwork.getCurrentArtwork(this)

    if (artwork == null) {
      val msg = resources.getString(R.string.no_artwork)
      Log.w(TAG, msg)
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
      return
    }
    val title: String? = artwork.title
    val byline: String? = artwork.byline
    val image: Bitmap = try {
      BitmapFactory.decodeStream(contentResolver.openInputStream(MuzeiContract.Artwork.CONTENT_URI))
    } catch (e: FileNotFoundException) {
      Log.w(TAG, "Unable to decode stream into bitmap")
      return
    }
    val date = Date().toString()
    var filename = title?.trim() ?: ""

    if (byline != null) {
      filename += if (filename.isEmpty()) {
        byline.trim()
      } else {
        " by ${byline.trim()}"
      }
    }

    // filter out bad characters
    filename = filename.replace("[^A-Za-z0-9 ]".toRegex(), "_")

    filename += if (filename.isEmpty()) {
      "$date.png"
    } else {
      " - $date.png"
    }

    saveBitmapToFile(filename, image)

    finish()
  }

  private fun saveBitmapToFile(filename: String, image: Bitmap) {
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)

    try {
      file.outputStream().use {
        image.compress(Bitmap.CompressFormat.PNG, 100, it)
        Log.i(TAG, "Image saved to: $file")
      }
    } catch (e: Exception) {
      val msg = resources.getString(R.string.unable_to_save)
      Log.e(TAG, "$msg : ${e.message}")
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
      return
    }
    // add the file to the gallery
    MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)
    Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_SHORT).show()
  }

}
