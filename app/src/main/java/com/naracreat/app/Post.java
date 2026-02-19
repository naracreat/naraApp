package com.naracreat.app;

import com.google.gson.annotations.SerializedName;

public class Post {
    public String title;
    public String slug;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("video_url")
    public String videoUrl;

    @SerializedName("duration_minutes")
    public int durationMinutes;

    public int views;

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("created_at")
    public String createdAt;

    // kalau API ada description, aktifin:
    public String description;
}
