package pl.poznajapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pl.poznajapp.API.APIService
import pl.poznajapp.PoznajApp
import pl.poznajapp.R
import pl.poznajapp.adapter.StoryListAdapter
import pl.poznajapp.listeners.RecyclerViewItemClickListener
import pl.poznajapp.model.Story
import pl.poznajapp.view.base.BaseAppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by Rafał Gawlik on 13.08.17.
 */

class MainActivity : BaseAppCompatActivity() {

    private lateinit var adapter: StoryListAdapter
    private var stories: ArrayList<Story> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
        initListeners()
        startIntroAnimation()
    }

    private fun startIntroAnimation() {
        val actionbarSize = 1000
        mainToolbar.translationY = -actionbarSize.toFloat()
    }

    override fun onResume() {
        super.onResume()
        loadStories()
    }

    private fun setupView() {
        adapter = StoryListAdapter(applicationContext, stories)
        activityMainStoryList.layoutManager = LinearLayoutManager(applicationContext)
        activityMainStoryList.adapter = adapter
        activityMainStoryList.itemAnimator = DefaultItemAnimator()

        info.visibility = View.VISIBLE
    }

    private fun initListeners() {
        activityMainStoryList.addOnItemTouchListener(RecyclerViewItemClickListener(this,
                activityMainStoryList, object : RecyclerViewItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                startActivity(StoryDetailsActivity.getConfigureIntent(applicationContext, stories[position].id))
            }

            override fun onItemLongClick(view: View?, position: Int) {

            }
        }))
        info.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }
    }

    private fun loadStories() {
        if (isInternetEnable) {
            val service = PoznajApp.retrofit.create(APIService::class.java)

            showProgressDialog("", getString(R.string.download_stories))

            val storyListCall = service.listStories()
            storyListCall.enqueue(object : Callback<List<Story>> {
                override fun onResponse(call: Call<List<Story>>, response: Response<List<Story>>) {

                    response.body()?.let {
                        stories.clear()
                        stories.addAll(it)
                        adapter.notifyDataSetChanged()
                    }

                    hideProgressDialog()
                    showNoContent(stories.size == 0)
                    mainToolbar.animate()
                            .translationY(0F)
                            .setDuration(400)
                            .startDelay = 300

                }

                override fun onFailure(call: Call<List<Story>>, throwable: Throwable) {
                    hideProgressDialog()
                }
            })
        } else {
            Snackbar.make(findViewById(R.id.activity_main), getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.reload_content)) { loadStories() }
                    .show()
        }
    }

    private fun showNoContent(isListEmpty: Boolean) {
        activityMainStoryList.visibility = if (isListEmpty) View.GONE else View.VISIBLE
        activityMainNoContent.visibility = if (isListEmpty) View.VISIBLE else View.GONE
    }
}
