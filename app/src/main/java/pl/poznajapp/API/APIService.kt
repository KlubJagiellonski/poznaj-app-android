package pl.poznajapp.API

import pl.poznajapp.model.Point
import pl.poznajapp.model.Story
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Rafał Gawlik on 21.08.17.
 */

interface APIService {

    @GET("stories/?format=json")
    fun listStories(): Call<List<Story>>

    @GET("stories/{id}/?format=json")
    fun getStory(@Path("id") id: Int?): Call<Story>

    @GET("stories/{id}/points/?format=json")
    fun getStoryPoints(@Path("id") id: Int?): Call<List<Point>>

}
