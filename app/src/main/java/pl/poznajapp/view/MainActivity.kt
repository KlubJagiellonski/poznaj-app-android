package pl.poznajapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.patloew.rxlocation.RxLocation

import java.util.ArrayList

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import pl.poznajapp.API.APIService
import pl.poznajapp.BuildConfig
import pl.poznajapp.PoznajApp
import pl.poznajapp.R
import pl.poznajapp.adapter.StoryListAdapter
import pl.poznajapp.listeners.RecyclerViewItemClickListener
import pl.poznajapp.model.Story
import pl.poznajapp.view.base.BaseAppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Rafał Gawlik on 13.08.17.
 */

class MainActivity : BaseAppCompatActivity() {

    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var adapter: StoryListAdapter
    private lateinit var stories: MutableList<Story>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stories = ArrayList()
        setupView()
        initListeners()
    }

    override fun onResume() {
        super.onResume()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            checkLocationEnabled()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, InfoActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        adapter = StoryListAdapter(applicationContext, stories)
        activity_main_story_list_rv.layoutManager = LinearLayoutManager(applicationContext)
        activity_main_story_list_rv.adapter = adapter
        activity_main_story_list_rv.itemAnimator = DefaultItemAnimator()
    }

    private fun initListeners() {
        activity_main_story_list_rv.addOnItemTouchListener(RecyclerViewItemClickListener(this,
                activity_main_story_list_rv, object : RecyclerViewItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                startActivity(StoryDetailsActivity.getConfigureIntent(applicationContext, stories[position].id))
            }

            override fun onItemLongClick(view: View, position: Int) {

            }
        }))
    }

    private fun loadStories(location: Location) {
        if (isInternetEnable) {
            val service = PoznajApp.retrofit.create(APIService::class.java)

            showProgressDialog(null, getString(R.string.download_stories))

            val storyListCall = service.listStories(location.latitude, location.longitude)
            storyListCall.enqueue(object : Callback<List<Story>> {
                override fun onResponse(call: Call<List<Story>>, response: Response<List<Story>>) {
                    if (response.body() != null)
                        stories.clear()
                    stories.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()

                    showNoContent(stories.size == 0)
                    hideProgressDialog()
                }

                override fun onFailure(call: Call<List<Story>>, throwable: Throwable) {
                    hideProgressDialog()
                }
            })
        } else {
            Snackbar.make(findViewById(R.id.activity_main), getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.reload_content)) { v -> getLocation() }
                    .show()
        }
    }

    private fun showNoContent(isListEmpty: Boolean) {
        activity_main_story_list_rv.visibility = if (isListEmpty) View.GONE else View.VISIBLE
        activity_main_no_content_ll.visibility = if (isListEmpty) View.VISIBLE else View.GONE
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkLocationEnabled() {
        //location settings
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {

                    }

                    override fun onConnectionSuspended(i: Int) {
                        googleApiClient.connect()
                    }
                })
                .addOnConnectionFailedListener {

                }.build()

        builder.setAlwaysShow(true)

        googleApiClient.connect()
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())

        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> getLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                } catch (@SuppressLint("NewApi") e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }//TODO show toast - location turn off
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationEnabled()
                } else {
                    Snackbar.make(
                            findViewById(R.id.activity_main),
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.settings) { view ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null)
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                            .show()
                }
            }
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (shouldProvideRationale) {
            hideProgressDialog()
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) { view ->
                        // Request permission
                        ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_PERMISSIONS_REQUEST_CODE)
                    }
                    .show()
        } else {
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> getLocation()
                Activity.RESULT_CANCELED -> {
                }
            }//TODO show toast - location turn off
        }
    }

    private fun getLocation() {

        val rxLocation = RxLocation(this)
        val locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        rxLocation.location().updates(locationRequest).subscribe(object : Observer<Location> {
            override fun onSubscribe(d: Disposable) {
                showProgressDialog(null, getString(R.string.get_location))
            }

            override fun onNext(location: Location) {
                hideProgressDialog()
                loadStories(location)
            }

            override fun onError(e: Throwable) {
                hideProgressDialog()
            }

            override fun onComplete() {
                hideProgressDialog()
            }
        })
    }

    companion object {

        private val REQUEST_CHECK_SETTINGS = 1
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 2
    }
}
