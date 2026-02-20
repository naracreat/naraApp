package com.naracreat.app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostResponse {

    public int page;

    @SerializedName("totalPages")
    public int totalPages;

    public int total;

    public List<Post> items;
}
