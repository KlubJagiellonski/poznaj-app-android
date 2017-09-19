package pl.poznajapp.helpers

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.preference.PreferenceManager
import pl.poznajapp.R
import java.text.DateFormat
import java.util.*

/**
 * Created by Rafał Gawlik on 11.08.17.
 */

object Utils {
    val URL_POZNAJAPP_ABOUT = "https://www.pola-app.pl/m/about"
    val URL_POZNAJAPP_METHOD = "https://www.pola-app.pl/m/method"
    val URL_POZNAJAPP_KJ = "https://www.pola-app.pl/m/kj"
    val URL_POZNAJAPP_TEAM = "https://www.pola-app.pl/m/team"
    val URL_POZNAJAPP_PARTNERS = "https://www.pola-app.pl/m/partners"
    val POZNAJAPP_MAIL = "adresmailowy@gmail.com"
    val URL_POZNAJAPP_FB = "https://www.facebook.com/app.pola"
    val URL_POZNAJAPP_FB_PAGENAME = "app.pola"

    val TIMEOUT_SECONDS: Long = 20

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"

    /**
     * Returns true if requesting location updates, otherwise returns false.

     * @param context The [Context].
     */
    fun requestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun setRequestingLocationUpdates(context: Context, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply()
    }

    /**
     * Returns the `location` object as a human readable string.
     * @param location  The [Location].
     */
    fun getLocationText(location: Location?): String {
        return if (location == null)
            "Unknown location"
        else
            "(" + location!!.getLatitude() + ", " + location!!.getLongitude() + ")"
    }

    fun getLocationTitle(context: Context): String {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(Date()))
    }
}
