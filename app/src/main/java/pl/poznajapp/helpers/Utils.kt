package pl.poznajapp.helpers

import android.content.res.Resources

/**
 * Created by Rafał Gawlik on 11.08.17.
 */

object Utils {

    val TIMEOUT_SECONDS: Long = 20

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

}
