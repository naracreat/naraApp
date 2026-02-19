package com.naracreat.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private final ArrayList<Post> posts = new ArrayList<>();
    private PostAdapter adapter;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);

        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(posts);
        rv.setAdapter(adapter);

        loadFromWeb();
    }

    private void loadFromWeb() {
        title.setText("NaraApp (loading...)");

        new Thread(() -> {
            try {
                String html = WebFetch.download("https://narahentai.pages.dev/");
                ArrayList<String> mp4s = WebFetch.extractMp4Urls(html);

                ArrayList<Post> fresh = new ArrayList<>();
                for (int i = 0; i < mp4s.size(); i++) {
                    String url = mp4s.get(i);
                    fresh.add(new Post("Video #" + (i + 1), "From web", url));
                }

                runOnUiThread(() -> {
                    posts.clear();
                    posts.addAll(fresh);
                    adapter.notifyDataSetChanged();
                    title.setText("NaraApp (" + posts.size() + " video)");
                });

            } catch (Exception e) {
                runOnUiThread(() -> title.setText("NaraApp (error: " + e.getMessage() + ")"));
            }
        }).start();
    }
}
