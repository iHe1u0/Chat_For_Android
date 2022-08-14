package com.imorning.common.view

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {

    fun info(
        context: Context,
        title: String,
        message: String,
        cancelable: Boolean = false,
        positiveButtonString: String = context.getString(android.R.string.ok)
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(positiveButtonString) { _, _ ->
            }
            .show()
    }

}