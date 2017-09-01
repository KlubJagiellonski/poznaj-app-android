package pl.poznajapp.database;

import java.util.List;

import io.realm.Realm;
import pl.poznajapp.database.model.PointObject;
import pl.poznajapp.database.model.StoryObject;

/**
 * Created by Rafał Gawlik on 23.08.17.
 */

public class Database {

    public void saveStory(Integer id, String title, String description){
        Realm.deleteRealm(Realm.getDefaultConfiguration());
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        StoryObject story = realm.createObject(StoryObject.class);
        story.setId(id);
        story.setTitle(title);
        story.setDescription(description);
        realm.commitTransaction();
    }

}
