package com.naracreat.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Post {
    public String title;
    public String slug;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("video_url")
    public String videoUrl;

    @SerializedName("duration_minutes")
    public Integer durationMinutes;

    public Integer views;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("created_at")
    public String createdAt;

    // OPTIONAL dari API (kalau ada)
    public List<String> genres;
    public String genre;
    public String category;

    public String timeSrc() {
        return (publishedAt != null && !publishedAt.isEmpty()) ? publishedAt : createdAt;
    }
}
