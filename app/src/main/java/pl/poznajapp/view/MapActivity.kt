package pl.poznajapp.view

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

import java.util.ArrayList

import pl.poznajapp.API.APIService
import pl.poznajapp.PoznajApp
import pl.poznajapp.R
import pl.poznajapp.adapter.PointListAdapter
import pl.poznajapp.model.Feature
import pl.poznajapp.model.Point
import pl.poznajapp.view.base.BaseAppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * Created by Rafał Gawlik on 02.09.17.
 */

class MapActivity : BaseAppCompatActivity(), OnMapReadyCallback {

    private lateinit var adapter: PointListAdapter
    private lateinit var service: APIService

    private lateinit var googleMap: GoogleMap
    private lateinit var features: List<Feature>

    private var id: Int = 0
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if (intent.extras != null) {
            id = intent.getIntExtra(EXTRAS_STORY_ID, -1)
            title = intent.getStringExtra(EXTRAS_STORY_TITLE)
            setupView()

            features = ArrayList()
            service = PoznajApp.retrofit.create(APIService::class.java)
        } else {
            finish()
        }

    }

    private fun setupView() {
        val pointsList = findViewById<View>(R.id.activity_map_point_list_rv) as RecyclerView
        adapter = PointListAdapter(ArrayList(), object : PointListAdapter.OnItemClickListener {
            override fun onDetailsClick(feature: Feature, position: Int) {

                if (feature.properties.pointImages.size > 0)
                    startActivity(PointDetailsActivity.getConfigureIntent(
                            applicationContext,
                            feature.properties.pointImages[0].imageFile,
                            feature.properties.title,
                            feature.geometry.coordinates[1],
                            feature.geometry.coordinates[0],
                            feature.properties.description)
                    )
            }

            override fun onMoveClick(feature: Feature, position: Int) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                                feature.geometry.coordinates[1],
                                feature.geometry.coordinates[0]), 16.5f))
            }
        })

        pointsList.layoutManager = LinearLayoutManager(applicationContext)
        pointsList.adapter = adapter

        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.activity_map_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        styleMap(googleMap)

        if (isInternetEnable) {
            val pointListCall = service.getStoryPoints(id)
            pointListCall.enqueue(object : Callback<List<Point>> {
                override fun onResponse(call: Call<List<Point>>, response: Response<List<Point>>) {
                    Timber.d(response.message())
                    features = response.body()!![1].features

                    adapter.setPointList(response.body()!![1].features)
                    adapter.notifyDataSetChanged()

                    for (feature in response.body()!![1].features)
                        addMarker(feature)

                    zoomToPoint(LatLng(features[0].geometry.coordinates[1],
                            features[0].geometry.coordinates[0]))

                }

                override fun onFailure(call: Call<List<Point>>, t: Throwable) {
                    Timber.e(t)
                }
            })
        } else {
            Snackbar.make(
                    findViewById<View>(R.id.activity_main),
                    getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE)
                    .show()
        }
    }

    private fun zoomToPoint(latLng: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
    }

    private fun styleMap(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
            if (!success) {
                Timber.e("Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e(e)
        }

    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }

    private fun addMarker(feature: Feature) {
        val latLng = LatLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0])
        googleMap.addMarker(MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black))
        )
    }

    companion object {

        const val EXTRAS_STORY_ID = "EXTRAS_STORY_ID"
        const val EXTRAS_STORY_TITLE = "EXTRAS_STORY_TITLE"

        fun getConfigureIntent(context: Context, storyId: Int?, storyTitle: String): Intent {
            val intent = Intent(context, MapActivity::class.java)
            intent.putExtra(EXTRAS_STORY_ID, storyId)
            intent.putExtra(EXTRAS_STORY_TITLE, storyTitle)
            return intent
        }
    }
}
