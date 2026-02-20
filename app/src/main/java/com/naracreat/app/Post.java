package com.naracreat.app;

import com.google.gson.annotations.SerializedName;

public class Post {
    public String title;
    public String slug;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("video_url")
    public String videoUrl;

    public Integer views;

    @SerializedName("duration_minutes")
    public Integer durationMinutes;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("created_at")
    public String createdAt;

    // ✅ biar PlayerActivity/ProfileFragment ga error
    public String description;
}
