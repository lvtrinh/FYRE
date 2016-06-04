/******************************************************************************
 * PrivacyPolicy.java
 *
 * This is the activity the user is taken to when they are viewing the privacy
 * policy, which is generated on screen.
 ******************************************************************************/
package com.teamfyre.fyre;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class PrivacyPolicy extends AppCompatActivity {

    /**************************************************************************
     * onCreate()
     * 
     * This function sets up the activity. It populates the screen with the input
     * fields for the receipt's manual additions.
     * 
     * This function is called when the activity starts. For more on what this
     * means, see:
     * http://developer.android.com/training/basics/activity-lifecycle/starting.html
     * (protip: ctrl/cmd-click in android studio to open the link!)
     *
     * @param savedInstanceState The saved instance state
     **************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView privacyPolicy = (WebView) findViewById(R.id.webview);
        privacyPolicy.getSettings().setJavaScriptEnabled(true);
        privacyPolicy.setWebViewClient(new WebViewClient());

        // loads privacy html file for display on screen
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
