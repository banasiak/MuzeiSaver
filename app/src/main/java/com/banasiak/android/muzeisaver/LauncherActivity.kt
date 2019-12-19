package com.banasiak.android.muzeisaver

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.banasiak.android.muzeisaver.download.DownloadActivity

class LauncherActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Intent(this, DownloadActivity::class.java).also {
      it.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
      startActivity(it)
    }
    finish()
  }
}