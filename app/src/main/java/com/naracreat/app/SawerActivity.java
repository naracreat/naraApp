package com.naracreat.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class SawerActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView wv = new WebView(this);
        setContentView(wv);

        WebSettings s = wv.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);

        wv.setWebViewClient(new WebViewClient());
        String url = getIntent().getStringExtra("url");
        if (url == null || url.isEmpty()) url = "https://saweria.co/Narapoi";
        wv.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        WebView wv = (WebView) findViewById(android.R.id.content).getRootView();
        if (wv != null && wv.canGoBack()) {
            wv.goBack();
            return;
        }
        super.onBackPressed();
    }
}
