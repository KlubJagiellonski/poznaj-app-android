package pl.poznajapp.helpers;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Mikolaj Rodkiewicz on 10.09.2017.
 * I use this: https://stackoverflow.com/questions/34564211/open-facebook-page-in-facebook-app-if-installed-on-android
 */

public class FacebookPageUrl {
    public String getFacebookPageURL(Context context, String pageUrl, String pagaName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + "https://www.facebook.com/" + pagaName;
            } else { //older versions of fb app
                return "fb://page/" + pagaName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return pageUrl; //web url
        }
    }
}
