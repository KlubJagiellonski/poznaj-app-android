package pl.poznajapp.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_point_details.*

import pl.poznajapp.R
import pl.poznajapp.view.base.BaseAppCompatActivity

/**
 * Created by Rafał Gawlik on 08.09.17.
 */

class PointDetailsActivity : BaseAppCompatActivity() {

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_details)

        if (this.intent.extras != null) {
            val image = intent.getStringExtra(EXTRAS_POINT_IMAGE)
            val title = intent.getStringExtra(EXTRAS_POINT_TITLE)
            val description = intent.getStringExtra(EXTRAS_POINT_DESCRIPTION)
            this.latitude = intent.getDoubleExtra(EXTRAS_POINT_LATITUDE, 0.0)
            this.longitude = intent.getDoubleExtra(EXTRAS_POINT_LONGITUDE, 0.0)

            setupView(image, title, description)
        } else {
            finish()
        }

    }

    private fun setupView(image: String, title: String, description: String) {
        point_details_text_tv.text = description

        Picasso.with(applicationContext)
                .load(image)
                .placeholder(R.drawable.back)
                .into(point_details_back_iv)

        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityPointDetailsAction.setOnClickListener {
            onShowGoogleMapsClick()
        }

    }

    fun onShowGoogleMapsClick() {
        val uri = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    companion object {

        const val EXTRAS_POINT_IMAGE = "EXTRAS_POINT_IMAGE"
        const val EXTRAS_POINT_TITLE = "EXTRAS_POINT_TITLE"
        const val EXTRAS_POINT_DESCRIPTION = "EXTRAS_POINT_DESCRIPTION"
        const val EXTRAS_POINT_LATITUDE = "EXTRAS_POINT_LATITUDE"
        const val EXTRAS_POINT_LONGITUDE = "EXTRAS_POINT_LONGITUDE"

        fun getConfigureIntent(context: Context, image: String, title: String, latitude: Double?, longitude: Double?, details: String): Intent {
            val intent = Intent(context, PointDetailsActivity::class.java)
            intent.putExtra(EXTRAS_POINT_IMAGE, image)
            intent.putExtra(EXTRAS_POINT_TITLE, title)
            intent.putExtra(EXTRAS_POINT_LATITUDE, latitude)
            intent.putExtra(EXTRAS_POINT_LONGITUDE, longitude)
            intent.putExtra(EXTRAS_POINT_DESCRIPTION, details)
            return intent
        }
    }

}
