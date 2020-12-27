package com.banasiak.android.muzeisaver.download

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.banasiak.android.muzeisaver.R
import com.google.android.apps.muzei.api.MuzeiContract
import timber.log.Timber

class DownloadService : JobIntentService() {
  companion object {
    const val DOWNLOAD_COMPLETE_ID = 62181
    private const val JOB_ID = 1000
    private const val NOTIFICATION_TIMEOUT = 5 * 1000L //5 seconds

    fun enqueueWork(context: Context, intent: Intent) {
      enqueueWork(context, DownloadService::class.java, JOB_ID, intent)
    }
  }

  override fun onHandleWork(intent: Intent) {
    val data = requireNotNull(intent.data) { "Content URI must be provided" }
    downloadArtwork(data)
  }

  private fun downloadArtwork(fileUri: Uri) {
    val bitmap: Bitmap? = contentResolver.openInputStream(MuzeiContract.Artwork.CONTENT_URI).use {
      BitmapFactory.decodeStream(it)
    }
    if (bitmap == null) {
      notify(message = R.string.unable_to_save)
      return
    }
    when (saveBitmapToFile(fileUri, bitmap)) {
      true -> notify(message = R.string.success, uri = fileUri)
      false -> notify(message = R.string.unable_to_save)
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

  private fun notify(@StringRes message: Int, uri: Uri? = null) {
    if (uri == null) Timber.e(getString(message)) else Timber.i(getString(message))
    val notification = NotificationCompat.Builder(this, DOWNLOAD_COMPLETE_ID.toString())
      .setSmallIcon(R.drawable.ic_file_download)
      .setColor(ResourcesCompat.getColor(resources, R.color.ic_launcher_background, null))
      .setContentTitle(getString(R.string.app_name))
      .setContentText(getString(message))
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setTimeoutAfter(NOTIFICATION_TIMEOUT)
      .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && uri != null) {
          setLargeIcon(ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri)))
        }
      }
      .build()

    NotificationManagerCompat.from(this).apply {
      notify(DOWNLOAD_COMPLETE_ID, notification)
    }
  }

}