package com.banasiak.android.muzeisaver

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.apps.muzei.api.MuzeiContract
import timber.log.Timber

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

    launchFileChooser(filename = artwork.generateFilename())

  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != CREATE_FILE_REQUEST_CODE) {
      super.onActivityResult(requestCode, resultCode, data)
      return
    }

    if (resultCode != Activity.RESULT_OK) {
      showErrorAndFinish(R.string.unable_to_save)
      return
    }

    if (data?.data == null) {
      showErrorAndFinish(R.string.unable_to_save)
      return
    }

    Intent(this, DownloadService::class.java).also {
      it.data = data.data
      startService(it)
    }

    finish()

  }

  private fun launchFileChooser(filename: String) {
    Intent(Intent.ACTION_CREATE_DOCUMENT)
      .addCategory(Intent.CATEGORY_OPENABLE)
      .setType(MIME_TYPE)
      .putExtra(Intent.EXTRA_TITLE, filename)
      .also { startActivityForResult(it, CREATE_FILE_REQUEST_CODE) }
  }


  private fun showErrorAndFinish(@StringRes message: Int) {
    Timber.e(getString(message))
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    finish()
  }

}
