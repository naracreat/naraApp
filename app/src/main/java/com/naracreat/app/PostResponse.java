package com.naracreat.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostResponse {
    @SerializedName("page")
    public int page;

    @SerializedName("totalPages")
    public int totalPages;

    @SerializedName("total")
    public int total;

    @SerializedName("items")
    public List<Post> items;
}
