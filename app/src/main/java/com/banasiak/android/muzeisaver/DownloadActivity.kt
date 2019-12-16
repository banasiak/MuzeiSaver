package com.banasiak.android.muzeisaver

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.apps.muzei.api.Artwork
import com.google.android.apps.muzei.api.MuzeiContract
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.Date

class DownloadActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
  companion object {
    private const val TAG = "DownloadActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    downloadArtwork()
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
  }

  override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    val msg = resources.getString(R.string.no_permissions)
    Log.e(TAG, msg)
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    finish()
  }

  override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    downloadArtwork()
  }

  private fun downloadArtwork() {
    if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      val artwork: Artwork? = MuzeiContract.Artwork.getCurrentArtwork(this)

      if (artwork == null) {
        val msg = resources.getString(R.string.no_artwork)
        Log.w(TAG, msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        finish()
        return
      }
      val title: String? = artwork.title
      val byline: String? = artwork.byline
      val image: Bitmap = try {
        BitmapFactory.decodeStream(contentResolver.openInputStream(MuzeiContract.Artwork
            .CONTENT_URI))
      } catch (e: Exception) {
        val msg = resources.getString(R.string.unable_to_save)
        Log.e(TAG, "$msg : ${e.message}")
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        finish()
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

    } else {
      EasyPermissions.requestPermissions(
          this,
          getString(R.string.rationale),
          0,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
    }
  }

  private fun saveBitmapToFile(filename: String, image: Bitmap) {
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)

    try {
      file.outputStream().use {
        image.compress(Bitmap.CompressFormat.PNG, 100, it)
        Log.i(TAG, "Image saved to: $file")
      }
    } catch (e: Throwable) {
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
