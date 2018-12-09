package pl.poznajapp.view


import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.toolbar.*
import pl.poznajapp.R
import pl.poznajapp.view.base.BaseAppCompatActivity

/**
 * Created by Mikołaj Rodkiewicz on 09.09.2017.
 */

class InfoActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)


        mainToolbarBack.visibility = View.VISIBLE
        mainToolbarTitle.text = "INFO"
        mainToolbarBack.setOnClickListener { finish() }
    }
}
