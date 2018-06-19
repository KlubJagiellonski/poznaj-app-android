package pl.poznajapp.view

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView


import pl.poznajapp.R
import pl.poznajapp.helpers.Utils
import pl.poznajapp.view.base.BaseAppCompatActivity

/**
 * Created by Mikołaj Rodkiewicz on 09.09.2017.
 */

class InfoActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
    }
}
