package pl.poznajapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Rafał Gawlik on 11.08.17.
 */

class Image {

    @SerializedName("image_file")
    @Expose
    var imageFile: String? = null
    @SerializedName("copyright")
    @Expose
    var copyright: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null

}

