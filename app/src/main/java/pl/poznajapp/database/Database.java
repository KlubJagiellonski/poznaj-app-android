package pl.poznajapp.database;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Rafał Gawlik on 19.03.17.
 */

public class Database {

    public static final String PREFERENCES_NAME = "poznajapp";
    public static final int MODE = Activity.MODE_PRIVATE;

    public static final String PREFERENCES_CURRENT_STORY = "current_story";
    public static final String PREFERENCES_STORY_POINTS = "story_points";


    public static void setCurrectStory(SharedPreferences preferences, int id){
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putInt(PREFERENCES_CURRENT_STORY, id);
        preferencesEditor.commit();
    }

    public static int getCurrectStory(SharedPreferences preferences){
        return  preferences.getInt(PREFERENCES_CURRENT_STORY, -1);
    }
}
