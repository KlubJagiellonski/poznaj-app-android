package pl.poznajapp.network;

import java.util.List;

import pl.poznajapp.pojo.Image;
import pl.poznajapp.pojo.Point;
import pl.poznajapp.pojo.Story;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Rafał Gawlik on 30.11.2016.
 */

//TODO implement
public interface API {


    @GET("images/")
    Call<List<Image>> listImages();

    @GET("points/")
    Call<List<Point>> listPoints();

    @GET("stories/")
    Call<List<Story>> listStories();

}
