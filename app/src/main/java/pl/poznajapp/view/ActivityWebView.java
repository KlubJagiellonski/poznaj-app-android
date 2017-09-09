package pl.poznajapp.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


import pl.poznajapp.R;
import pl.poznajapp.helpers.Utils;

/**
 * Created by mr on 09.09.2017.
 */

public class ActivityWebView extends Activity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url")) {
            webView.loadUrl(getIntent().getExtras().getString("url"));
        } else {
            webView.loadUrl(Utils.INSTANCE.getURL_POZNAJAPP_ABOUT());
        }
        webView.setWebChromeClient(new WebChromeClient());
    }
}
