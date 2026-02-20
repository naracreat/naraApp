package com.naracreat.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Post {
    public String title;

    @SerializedName("videoUrl")
    public String videoUrl;

    @SerializedName("thumbnailUrl")
    public String thumbnailUrl;

    // view bisa int atau Integer dari API
    public Integer views;

    @SerializedName("createdAt")
    public String createdAt;

    @SerializedName("publishedAt")
    public String publishedAt;

    // Genre bisa 1 string, atau list string (tergantung API kamu)
    public String genre;
    public String category;
    public List<String> genres;
}
