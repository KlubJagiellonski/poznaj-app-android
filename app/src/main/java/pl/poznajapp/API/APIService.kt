package pl.poznajapp.API

import pl.poznajapp.model.Story
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Rafał Gawlik on 11.08.17.
 */

interface APIService {

    @GET("stories")
    fun listStories(): Call<List<Story>>

}
