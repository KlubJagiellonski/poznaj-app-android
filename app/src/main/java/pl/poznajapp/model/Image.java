package pl.poznajapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafał Gawlik on 22.08.17.
 */

public class Image {

    @SerializedName("image_file")
    @Expose
    private String imageFile;
    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("title")
    @Expose
    private String title;

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}


