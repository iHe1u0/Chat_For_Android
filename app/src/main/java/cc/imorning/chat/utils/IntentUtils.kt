package cc.imorning.chat.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import cc.imorning.chat.R

object IntentUtils {

    fun openBrowser(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.browser_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}