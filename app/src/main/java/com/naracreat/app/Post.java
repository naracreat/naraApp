package com.naracreat.app;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName(value = "id", alternate = {"_id"})
    public String id;

    @SerializedName(value = "title", alternate = {"name"})
    public String title;

    // thumbnail keys yang sering kepakai
    @SerializedName(value = "thumbnailUrl", alternate = {
            "thumbnail_url","thumbnail","thumb","poster","poster_url","image","image_url","cover","cover_url"
    })
    public String thumbnailUrl;

    // video keys yang sering kepakai
    @SerializedName(value = "videoUrl", alternate = {
            "video_url","video","file","file_url","source","src","play_url","stream_url"
    })
    public String videoUrl;

    // time keys
    @SerializedName(value = "createdAt", alternate = {"created_at","created"})
    public String createdAt;

    @SerializedName(value = "publishedAt", alternate = {"published_at","published","date"})
    public String publishedAt;

    // views keys
    @SerializedName(value = "views", alternate = {"view","viewCount","view_count"})
    public Integer views;
}
