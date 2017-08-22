package pl.poznajapp.API;

import java.util.List;

import pl.poznajapp.model.Story;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Rafał Gawlik on 21.08.17.
 */

public interface APIService {

    @GET("stories")
    Call<List<Story>> listStories(@Query("lat") Double latitude, @Query("long") Double longitude);

}
