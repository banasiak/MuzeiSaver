package com.banasiak.android.muzeisaver.quicksettings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.banasiak.android.muzeisaver.R

class QuickSettingsDialog : AppCompatDialogFragment() {

  interface DialogListener {
    fun onPositiveClick()
    fun onNegativeClick()
  }

  private lateinit var _context: Context
  private lateinit var listener: DialogListener
  private lateinit var dialogView: View

  fun initialize(context: Context, listener: DialogListener) {
    this._context = context
    this.listener = listener
    this.isCancelable = false
  }

  @SuppressLint("InflateParams")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    check(::_context.isInitialized && ::listener.isInitialized) {
      "QuickSettingsDialog not properly initialized!"
    }

    dialogView = LayoutInflater.from(_context).inflate(R.layout.dialog, null)

    val alertBuilder = AlertDialog.Builder(_context).apply {
      setView(dialogView)
      setPositiveButton(R.string.show) { _, _ -> listener.onPositiveClick() }
      setNegativeButton(R.string.hide) { _, _ -> listener.onNegativeClick() }
    }

    return alertBuilder.create()
  }

}