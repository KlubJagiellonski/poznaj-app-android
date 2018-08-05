package pl.poznajapp.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso

import pl.poznajapp.R
import pl.poznajapp.view.base.BaseAppCompatActivity
import timber.log.Timber

/**
 * Created by Rafał Gawlik on 08.09.17.
 */

class PointDetailsActivity : BaseAppCompatActivity() {

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_details)

        if (intent.extras != null) {
            val image = intent.getStringExtra(EXTRAS_POINT_IMAGE)
            val title = intent.getStringExtra(EXTRAS_POINT_TITLE)
            val description = intent.getStringExtra(EXTRAS_POINT_DESCRIPTION)
            latitude = intent.getDoubleExtra(EXTRAS_POINT_LATLITUDE, 0.0)
            longitude = intent.getDoubleExtra(EXTRAS_POINT_LONGITUDE, 0.0)

            setupView(image, title, description)
        } else {
            finish()
        }

    }

    private fun setupView(image: String, title: String, description: String) {
        val pointIv = findViewById<ImageView>(R.id.point_details_back_iv)
        val pointDetailsTv = findViewById<TextView>(R.id.point_details_text_tv)

        pointDetailsTv.text = description

        Picasso.with(applicationContext).load(image).into(pointIv)

        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        Timber.i("onStop")
        super.onStop()
    }

    fun onShowGoogleMapsClick(view: View) {
        val uri = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    companion object {

        val EXTRAS_POINT_IMAGE = "EXTRAS_POINT_IMAGE"
        val EXTRAS_POINT_TITLE = "EXTRAS_POINT_TITLE"
        val EXTRAS_POINT_DESCRIPTION = "EXTRAS_POINT_DESCRIPTION"
        val EXTRAS_POINT_LATLITUDE = "EXTRAS_POINT_LATLITUDE"
        val EXTRAS_POINT_LONGITUDE = "EXTRAS_POINT_LONGITUDE"


        fun getConfigureIntent(context: Context, image: String, title: String, latitude: Double?, longitude: Double?, details: String): Intent {
            val intent = Intent(context, PointDetailsActivity::class.java)
            intent.putExtra(EXTRAS_POINT_IMAGE, image)
            intent.putExtra(EXTRAS_POINT_TITLE, title)
            intent.putExtra(EXTRAS_POINT_LATLITUDE, latitude)
            intent.putExtra(EXTRAS_POINT_LONGITUDE, longitude)
            intent.putExtra(EXTRAS_POINT_DESCRIPTION, details)
            return intent
        }
    }

}
