package cc.imorning.common.utils

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import cc.imorning.chat.App
import java.io.InputStream


class ImageUtils {

    companion object {
        private const val TAG = "ImageUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ImageUtils()
        }
    }

    fun getDrawable(inputStream: InputStream?): Drawable? {
        return BitmapDrawable.createFromResourceStream(
            App.getContext().resources,
            null,
            inputStream,
            null
        )
    }

}