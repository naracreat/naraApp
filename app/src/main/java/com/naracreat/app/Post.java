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

    @SerializedName("published_at")
    public String publishedAt;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("duration_minutes")
    public Integer durationMinutes;

    // optional kalau nanti ada
    public String description;

    public String timeSrc() {
        if (createdAt != null && createdAt.trim().length() > 0) return createdAt;
        if (publishedAt != null && publishedAt.trim().length() > 0) return publishedAt;
        return null;
    }
}
