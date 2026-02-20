package com.naracreat.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private void load(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, f)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottomNav);

        load(new HomeFragment());

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) load(new HomeFragment());
            else if (id == R.id.nav_update) load(new UpdateFragment());
            else if (id == R.id.nav_premium) load(new PremiumFragment());
            else if (id == R.id.nav_profile) load(new ProfileFragment());
            return true;
        });
    }
}
