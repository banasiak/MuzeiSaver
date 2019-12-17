package com.banasiak.android.muzeisaver

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.apps.muzei.api.MuzeiContract
import timber.log.Timber

class DownloadService : IntentService("MuzeiSaverDownloadService") {
  companion object {
    private const val NOTIFICATION_ID = 62180
  }

  override fun onHandleIntent(intent: Intent?) {

    requireNotNull(intent?.data) { "File URI must be provided" }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = NotificationManagerCompat.from(this)
      val notificationChannel = NotificationChannel(NOTIFICATION_ID.toString(), getString(R.string.downloads), NotificationManager.IMPORTANCE_LOW)
      notificationManager.createNotificationChannels(listOf(notificationChannel))
    }

    val notification = NotificationCompat.Builder(this, NOTIFICATION_ID.toString())
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setColor(ResourcesCompat.getColor(resources, R.color.ic_launcher_background, null))
      .setContentText(getString(R.string.downloading_artwork))
      .setProgress(0, 0, true)
      .build()

    startForeground(NOTIFICATION_ID, notification)
    downloadArtwork(intent?.data)
    stopForeground(false)

  }

  private fun downloadArtwork(fileUri: Uri?) {
    val bitmap: Bitmap? = contentResolver.openInputStream(MuzeiContract.Artwork.CONTENT_URI).use {
      BitmapFactory.decodeStream(it)
    }

    if (fileUri == null || bitmap == null) {
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