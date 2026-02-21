package com.naracreat.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class SawerActivity extends AppCompatActivity {

    public static final String SAWER_URL = "https://saweria.co/Narapoi";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sawer);

        WebView web = findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.loadUrl(SAWER_URL);
    }

    @Override
    public void onBackPressed() {
        WebView web = findViewById(R.id.web);
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
