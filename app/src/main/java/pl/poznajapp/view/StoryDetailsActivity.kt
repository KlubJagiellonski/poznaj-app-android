package pl.poznajapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_story_details.*

import pl.poznajapp.API.APIService
import pl.poznajapp.PoznajApp
import pl.poznajapp.R
import pl.poznajapp.model.Story
import pl.poznajapp.view.base.BaseAppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * Created by Rafał Gawlik on 22.08.17.
 */

class StoryDetailsActivity : BaseAppCompatActivity() {

    private lateinit var story: Story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_details)

        if (intent.extras != null) {
            setupView()
            loadStory(intent.getIntExtra(EXTRAS_STORY_ID, -1))
        } else {
            finish()
        }
    }

    private fun setupView() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findViewById<FloatingActionButton>(R.id.story_details_walk_fab)
                .setOnClickListener { startActivity(MapActivity.getConfigureIntent(this, story.id, story.title)) }
    }

    private fun loadStory(id: Int) {
        if (id == -1)
            finish()

        if (isInternetEnable) {
            val service = PoznajApp.retrofit.create(APIService::class.java)

            showProgressDialog("", getString(R.string.download_story))
            val storyCall = service.getStory(id)
            storyCall.enqueue(object : Callback<Story> {
                override fun onResponse(call: Call<Story>, response: Response<Story>) {
                    Timber.d(response.message())

                    story = response.body()!!
                    supportActionBar!!.title = story.title
                    story_details_text_tv.text = story.description

                    Picasso.with(applicationContext).load(story.storyImages[0].imageFile).into(story_details_back_iv)
                    if (progressDialog.isShowing)
                        hideProgressDialog()
                }

                override fun onFailure(call: Call<Story>, t: Throwable) {
                    Timber.e(t)
                    if (progressDialog.isShowing)
                        hideProgressDialog()
                }
            })
        } else {
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE)
                    .show()
        }
    }

    companion object {

        const val EXTRAS_STORY_ID = "EXTRAS_STORY_ID"

        fun getConfigureIntent(context: Context, storyId: Int?): Intent {
            val intent = Intent(context, StoryDetailsActivity::class.java)
            intent.putExtra(EXTRAS_STORY_ID, storyId)
            return intent
        }
    }
}