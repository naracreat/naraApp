package com.naracreat.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PremiumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("Premium (tanpa iklan) - coming soon\n\nDi sini nanti bisa integrasi payment.");
        tv.setTextColor(getResources().getColor(R.color.text));
        tv.setTextSize(18f);
        tv.setPadding(30, 30, 30, 30);
        tv.setBackgroundColor(getResources().getColor(R.color.bg));
        setContentView(tv);
    }
}
