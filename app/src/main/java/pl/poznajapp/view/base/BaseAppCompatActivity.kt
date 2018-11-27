package pl.poznajapp.view.base

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by Rafał Gawlik on 21.08.17.
 */

@SuppressLint("Registered")
open class BaseAppCompatActivity : AppCompatActivity() {

    protected lateinit var progressDialog: ProgressDialog

    protected val isInternetEnable: Boolean
        get() {
            val conMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = conMgr.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog = ProgressDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showProgressDialog(title: String, message: String) {
        if (!progressDialog.isShowing) {
            progressDialog.setTitle(title)
            progressDialog.setMessage(message)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

}
