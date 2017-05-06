package pl.poznajapp.network;

import java.util.List;

import pl.poznajapp.pojo.FeatureCollection;
import pl.poznajapp.pojo.Image;
import pl.poznajapp.pojo.Point;
import pl.poznajapp.pojo.Story;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Rafał Gawlik on 30.11.2016.
 */

public interface API {

    @GET("images/?format=json")
    Call<List<Image>> listImages();

    @GET("images/{id}?format=json")
    Call<Image> getImage(@Path("id") Integer id);

    @GET("points/{id}?format=json")
    Call<Point> getPoint(@Path("id") Integer id);

    @GET("stories/{id}/points?format=json")
    Call<FeatureCollection> getStoryPoints(@Path("id") Integer id);

    @GET("stories/?format=json")
    Call<List<Story>> listStories();

    @GET("stories/{id}?format=json")
    Call<Story> getStory(@Path("id") Integer id);
}
