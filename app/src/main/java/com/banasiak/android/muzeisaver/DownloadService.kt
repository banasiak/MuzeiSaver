package com.banasiak.android.muzeisaver

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.apps.muzei.api.MuzeiContract
import timber.log.Timber

class DownloadService : IntentService("DownloadService") {

  companion object {
    const val NOTIFICATION_ID = 62180
  }

  override fun onHandleIntent(intent: Intent?) {
    val data = requireNotNull(intent?.data) { "File URI must be provided" }
    val notification = NotificationCompat.Builder(this, NOTIFICATION_ID.toString())
      .setSmallIcon(R.drawable.ic_file_download)
      .setColor(ResourcesCompat.getColor(resources, R.color.ic_launcher_background, null))
      .setContentTitle(getString(R.string.downloading_artwork))
      .setProgress(0, 0, true)
      .build()
    startForeground(NOTIFICATION_ID, notification)
    downloadArtwork(data)
    stopForeground(true)
  }

  private fun downloadArtwork(fileUri: Uri) {
    val bitmap: Bitmap? = contentResolver.openInputStream(MuzeiContract.Artwork.CONTENT_URI).use {
      BitmapFactory.decodeStream(it)
    }
    if (bitmap == null) {
      showToast(message = R.string.unable_to_save, isError = true)
      return
    }
    when (saveBitmapToFile(fileUri, bitmap)) {
      true -> showToast(message = R.string.success)
      false -> showToast(message = R.string.unable_to_save, isError = true)
    }
  }

  private fun saveBitmapToFile(fileUri: Uri, image: Bitmap): Boolean {
    return try {
      contentResolver.openOutputStream(fileUri).use {
        image.compress(Bitmap.CompressFormat.PNG, 100, it)
        Timber.i("Image saved to: ${fileUri.path}")
        true
      }
    } catch (e: Throwable) {
      Timber.e(e, "Unable to save bitmap")
      false
    }
  }

  private fun showToast(@StringRes message: Int, isError: Boolean = false) {
    if (isError) Timber.e(getString(message)) else Timber.i(getString(message))
    Handler(Looper.getMainLooper()).post { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
  }

}