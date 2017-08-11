package pl.poznajapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Rafał Gawlik on 11.08.17.
 */

class Story {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("duration")
    @Expose
    var duration: String? = null
    @SerializedName("story_images")
    @Expose
    var storyImages: List<Image>? = null

}
