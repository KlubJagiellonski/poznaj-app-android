package pl.poznajapp.view


import android.os.Bundle
import pl.poznajapp.R
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
