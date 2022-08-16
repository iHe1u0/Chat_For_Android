package cc.imorning.chat.view.ui

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils{

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

