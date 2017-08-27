package pl.poznajapp.view.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pl.poznajapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Rafał Gawlik on 21.08.17.
 */

public class BaseAppCompatActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        progressDialog = new ProgressDialog(this);
    }

    public void showProgressDialog(String title, String message){
        if (!progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void hideProgressDialog(){
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
