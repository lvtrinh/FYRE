package com.teamfyre.fyre;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import java.io.File;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView privacyPolicy = (WebView) findViewById(R.id.webview);
        privacyPolicy.getSettings().setJavaScriptEnabled(true);
        privacyPolicy.setWebChromeClient(new WebChromeClient());

        privacyPolicy.loadUrl("file:///android_asset/privacy_policy/index.html");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
