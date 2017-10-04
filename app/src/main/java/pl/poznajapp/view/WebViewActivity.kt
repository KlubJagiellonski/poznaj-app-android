package pl.poznajapp.view

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView


import pl.poznajapp.R
import pl.poznajapp.helpers.Utils
import pl.poznajapp.view.base.BaseAppCompatActivity

/**
 * Created by Mikołaj Rodkiewicz on 09.09.2017.
 */

class WebViewActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        if (intent != null && intent.extras != null && intent.extras!!.containsKey("title")) {
            supportActionBar!!.setTitle(intent.extras!!.getString("title"))
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val webView = findViewById<View>(R.id.activity_web_view) as WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        if (intent != null && intent.extras != null && intent.extras!!.containsKey("url")) {
            webView.loadUrl(intent.extras!!.getString("url"))
        } else {
            webView.loadUrl(Utils.URL_POZNAJAPP_ABOUT)
        }
        webView.webChromeClient = WebChromeClient()
    }
}
