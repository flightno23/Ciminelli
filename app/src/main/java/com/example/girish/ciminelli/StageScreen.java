package com.example.girish.ciminelli;

import android.app.ProgressDialog;
import android.content.pm.PackageInstaller;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by girish on 9/28/15.
 */
public class StageScreen extends ActionBarActivity{


    EditText viewComment;

    Button stageButton;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LinearLayout layout_main=(LinearLayout) findViewById(R.id.action_bar_all);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        TextView action=(TextView) findViewById(R.id.textView_action);
        action.setText(SessionDetails.project_names);
        setContentView(R.layout.stage_screen);
        Intent i= getIntent();
        String comments = i.getStringExtra("actual_comments");


        viewComment = (EditText) findViewById(R.id.view_comment);
        viewComment.setText(comments);

        stageButton = (Button) findViewById(R.id.stage_button);

        if (SessionDetails.verified) {

            stageButton.setText("Mark Stage as Verified");

        } else {

            stageButton.setText("Mark Stage as Tested");

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Progress Dialog initialization and config */
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_logout:
                logout();   // log out method is called when user chooses to log out
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* logout method for the user when he wants to logout of the session */
    private void logout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to log out?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                SessionDetails.assetCode = "";


                Intent intent = new Intent(StageScreen.this, MainActivity.class);

                startActivity(intent);

                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /* Method that runs when the save comment button is selected */
    public void saveComment(View view) {

        // final String unitNo = getIntent().getExtras().getString("unit_no");
        final String stageNumber = getIntent().getExtras().getString("stage_number");
        final String comments = viewComment.getText().toString();

        /* check if user has permissions to save the comment */
        /*if (Constants.comments == false) {
            Toast.makeText(this, "You don't have permissions to save comments", Toast.LENGTH_LONG).show();
            return;
        } */

        showpDialog();

        StringRequest sr = new StringRequest(Request.Method.POST,
                "http://www.drones.cse.buffalo.edu/ciminelli/stagescreen/update_stage_comment.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {   /* Successfull response listener */
                        hidepDialog();

                        Intent newIntent = new Intent(StageScreen.this, AssetInformation.class);
                        startActivity(newIntent);
                        finish();

                    }
                }, new Response.ErrorListener() {   /* Error Listener (Bad network connection, etc) */
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Toast.makeText(StageScreen.this, "Some Problem with network....", Toast.LENGTH_LONG).show();

                Log.d("Volley:", error.toString());
            }
        }) {
            /* POST parameters added to this http request */
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                /* Pass in the POST parameters */
                params.put("unit_no", SessionDetails.unitNo);
                params.put("stage_number", stageNumber);
                params.put("stage_comments", comments);

                return params;

            }
        };

        AppController.getInstance().addToRequestQueue(sr);


    }

    /* Mark Stage method */
    public void markStage(View view) {

        final String stageNumber = getIntent().getExtras().getString("stage_number");

        showpDialog();

        StringRequest sr = new StringRequest(Request.Method.POST,
                "http://www.drones.cse.buffalo.edu/ciminelli/stagescreen/update_stage.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {   /* Successfull response listener */
                        hidepDialog();

                        Intent newIntent = new Intent(StageScreen.this, AssetInformation.class);
                        startActivity(newIntent);
                        finish();

                    }
                }, new Response.ErrorListener() {   /* Error Listener (Bad network connection, etc) */
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Toast.makeText(StageScreen.this, "Some Problem with network....", Toast.LENGTH_LONG).show();

                Log.d("Volley:", error.toString());
            }
        }) {
            /* POST parameters added to this http request */
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                /* Pass in the POST parameters */
                params.put("unit_no", SessionDetails.unitNo);
                params.put("stage_number", stageNumber);

                if (SessionDetails.verified) {
                    params.put("completed", "2");
                } else {
                    params.put("completed", "1");
                }

                return params;

            }
        };

        AppController.getInstance().addToRequestQueue(sr);





    }

    /*
    * Method that will render the progress Dialog in the UI
    */
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /*
     * Method to hide the progress Dialog from the UI
     */
    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
