package com.banasiak.android.muzeisaver.quicksettings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class LongPressActivity : AppCompatActivity(), QuickSettingsDialog.DialogListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val dialog = QuickSettingsDialog()
    dialog.initialize(this, this)
    dialog.show(supportFragmentManager, "QuickSettingsDialog")
  }

  override fun onPositiveClick() {
    Timber.i("TODO: show launcher icon")
    finish()
  }

  override fun onNegativeClick() {
    Timber.i("TODO: hide launcher icon")
    finish()
  }


}