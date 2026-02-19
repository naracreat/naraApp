package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        bottomNav = findViewById(R.id.bottomNav);

        setupTabs();
        setupBottomNav();

        // default: Beranda
        openFragment(new HomeFragment());
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Ikuti"));
        tabLayout.addTab(tabLayout.newTab().setText("Untuk Anda"));
        tabLayout.addTab(tabLayout.newTab().setText("Anime"));
        tabLayout.addTab(tabLayout.newTab().setText("Gratis"));
        tabLayout.addTab(tabLayout.newTab().setText("Dracin"));
        tabLayout.addTab(tabLayout.newTab().setText("Sorotan"));

        // biarin aja (request 1: pencarian biarin)
        // kalau mau tab filter beneran nanti tinggal map ke endpoint yang beda
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                openFragment(new HomeFragment());
                return true;
            }

            if (id == R.id.nav_search) {
                openFragment(new SimpleFragment("Cari (coming soon)"));
                return true;
            }

            if (id == R.id.nav_premium) {
                startActivity(new Intent(this, PremiumActivity.class));
                return false; // biar gak “nyangkut” tab premium
            }

            if (id == R.id.nav_schedule) {
                openFragment(new SimpleFragment("Jadwal (coming soon)"));
                return true;
            }

            if (id == R.id.nav_me) {
                openFragment(new SimpleFragment("Saya (coming soon)"));
                return true;
            }

            return false;
        });
    }

    private void openFragment(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }
}
