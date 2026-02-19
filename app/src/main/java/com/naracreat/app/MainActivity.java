package com.naracreat.app;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Post> dummy = new ArrayList<>();
        dummy.add(new Post("Postingan 1", "Channel • 2 jam lalu"));
        dummy.add(new Post("Postingan 2", "Channel • 5 jam lalu"));
        dummy.add(new Post("Postingan 3", "Channel • kemarin"));

        rv.setAdapter(new PostAdapter(dummy));
    }
}
