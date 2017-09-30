package pl.poznajapp.helpers

import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by Mikolaj Rodkiewicz on 10.09.2017.
 * I use this: https://stackoverflow.com/questions/34564211/open-facebook-page-in-facebook-app-if-installed-on-android
 */

class FacebookPageUrl {
    fun getFacebookPageURL(context: Context, pageUrl: String, pagaName: String): String {
        var packageManager = context.packageManager
        try {
            var versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            return if (versionCode >= 3002850) { //newer versions of fb app
                "fb://facewebmodal/f?href=" + "https://www.facebook.com/" + pagaName
            } else { //older versions of fb app
                "fb://page/" + pagaName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return pageUrl //web url
        }

    }
}
