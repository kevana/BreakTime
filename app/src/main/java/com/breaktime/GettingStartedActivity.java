package com.breaktime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class GettingStartedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);
        WebView webView = (WebView) findViewById(R.id.gettingStartedView);
        webView.loadUrl("file:///android_asset/getting-started.html");
    }

}
