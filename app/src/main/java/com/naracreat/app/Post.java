package com.naracreat.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Post {

    @SerializedName("title")
    public String title;

    @SerializedName("slug")
    public String slug;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("video_url")
    public String videoUrl;

    @SerializedName("views")
    public Integer views;

    @SerializedName("duration_minutes")
    public Integer durationMinutes;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("created_at")
    public String createdAt;

    // OPTIONAL (biar HomeFragment ga error)
    @SerializedName("genres")
    public List<String> genres;

    @SerializedName("genre")
    public String genre;

    @SerializedName("category")
    public String category;
}
