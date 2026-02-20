package com.naracreat.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // default: Home
        if (savedInstanceState == null) {
            replace(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replace(new HomeFragment());
                return true;
            } else if (id == R.id.nav_update) {
                replace(new UpdateFragment());
                return true;
            } else if (id == R.id.nav_premium) {
                replace(new PremiumFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                replace(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    private void replace(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, f)
                .commit();
    }
}
