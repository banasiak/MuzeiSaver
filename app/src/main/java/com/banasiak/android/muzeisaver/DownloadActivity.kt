package com.banasiak.android.muzeisaver

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.apps.muzei.api.Artwork
import com.google.android.apps.muzei.api.MuzeiContract
import timber.log.Timber
import java.util.Date

class DownloadActivity : AppCompatActivity() {
  companion object {
    private const val CREATE_FILE_REQUEST_CODE = 50165
    private const val MIME_TYPE = "image/png"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val artwork = MuzeiContract.Artwork.getCurrentArtwork(this)

    if (artwork == null) {
      showErrorAndFinish(R.string.no_artwork)
      return
    }

    val filename = generateFilename(artwork)
    launchFileChooser(filename)
  }

  private fun generateFilename(artwork: Artwork): String {
    val title: String? = artwork.title
    val byline: String? = artwork.byline
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
      " - $date.png"
    }

    return filename
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != CREATE_FILE_REQUEST_CODE) {
      super.onActivityResult(requestCode, resultCode, data)
      return
    }

    if (resultCode != Activity.RESULT_OK) {
      showErrorAndFinish(R.string.no_permissions)
      return
    }

    Timber.i("data: $data")

    val fileUri = data?.data

    // TODO: use workmanager to do this in the background
    val bitmap = MuzeiContract.Artwork.getCurrentArtworkBitmap(this)

    if (fileUri == null || bitmap == null) {
      showErrorAndFinish(R.string.unable_to_save)
    } else {
      saveBitmapToFile(fileUri, bitmap)
      //      MediaScannerConnection.scanFile(this, arrayOf(fileUri.path), null, null)
      Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
      finish()
    }

  }

  private fun launchFileChooser(filename: String) {
    Intent(Intent.ACTION_CREATE_DOCUMENT)
      .addCategory(Intent.CATEGORY_OPENABLE)
      .setType(MIME_TYPE)
      .putExtra(Intent.EXTRA_TITLE, filename)
      .also { startActivityForResult(it, CREATE_FILE_REQUEST_CODE) }
  }

  private fun saveBitmapToFile(fileUri: Uri, image: Bitmap) {
    try {
      contentResolver.openOutputStream(fileUri).use {
        image.compress(Bitmap.CompressFormat.PNG, 100, it)
        Timber.i("Image saved to: ${fileUri.path}")
      }
    } catch (e: Throwable) {
      Timber.e(e, "Unable to save bitmap")
      return
    }
  }

  private fun showErrorAndFinish(@StringRes message: Int) {
    Timber.e(getString(message))
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    finish()
  }


}
