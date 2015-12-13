package com.example.girish.ciminelli;

import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.girish.ciminelli.SessionDetails.FIRST_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.FOURTH_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.SECOND_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.SIXTH_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.THIRD_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.FIFTH_COLUMN;

/**
 * Created by girish on 9/28/15.
 */
public class AssetInformation extends ActionBarActivity {

    private Dialog loadingDialog;

    private ArrayList<HashMap<String, String>> list;

    ListView listView;
    String com;
    TextView unitNumber, location, assetName;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asset_screen);
        LinearLayout layout_main=(LinearLayout) findViewById(R.id.action_bar_all);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        TextView action=(TextView) findViewById(R.id.textView_action);
        action.setText(SessionDetails.project_names);

        /* SecondScreen is the parent of AssetInformation class */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Progress Dialog config */
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        /*Initializing all the text boxes*/

        unitNumber = (TextView) findViewById(R.id.unit_no);
        location = (TextView) findViewById(R.id.location);

        assetName = (TextView) findViewById(R.id.asset_name);
        listView = (ListView) findViewById(R.id.listView1);


        /*
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                renderView();
            }
        });

        t.start();
    */
        /*
         * Method to populate the UI using volley
         */
        populateUI();


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

                SaveSharedPreference.clearUserName(AssetInformation.this);
                SessionDetails.assetCode = "";

                Intent intent = new Intent(AssetInformation.this, MainActivity.class);

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


    /*
     * Method to populate the Asset Information using volley for fast networking
     */
    private void populateUI() {

        showpDialog();

        String url_asset = "http://www.drones.cse.buffalo.edu/ciminelli/assetscreen/get_asset_details.php?unit_no="
                + SessionDetails.unitNo + "&" + "project_name=" + SessionDetails.project_names;

        String url_stage = "http://www.drones.cse.buffalo.edu/ciminelli/assetscreen/get_stage_details.php?unit_no="
                + SessionDetails.unitNo + "&" + "project_name=" + SessionDetails.project_names;

        list = new ArrayList<HashMap<String, String>>();

        /* request one - populate asset information */
        StringRequest request1 = new StringRequest(Request.Method.GET, url_asset,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray assets = json.getJSONArray("assets");
                            JSONObject temp = assets.getJSONObject(0);

                            unitNumber.setText(temp.getString("unit_no"));
                            assetName.setText(temp.getString("asset_name"));
                            location.setText(temp.getString("location"));

                        } catch (JSONException e) {
                            // fill error handling code later
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // fill error handling code later
            }
        });

        /* request two - populate stage information */
        StringRequest request2 = new StringRequest(Request.Method.GET, url_stage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray stages = json.getJSONArray("stages");
                            JSONObject temp = null;


                            for (int i=0; i < stages.length(); i++) {

                                temp = stages.getJSONObject(i);
                                HashMap<String, String> tempMap = new HashMap<String, String>();
                                tempMap.put(FIRST_COLUMN, temp.getString("unit_no"));
                                tempMap.put(SECOND_COLUMN, temp.getString("stage_number"));
                                tempMap.put(THIRD_COLUMN, temp.getString("stage_name"));
                                com= temp.getString("stage_comments");
                                if (com.length()<30)
                                {

                                    tempMap.put(FOURTH_COLUMN, temp.getString("stage_comments"));
                                    tempMap.put(SIXTH_COLUMN,temp.getString("stage_comments"));

                                }
                                else
                                {   com=com.substring(0,30)+"...";
                                    tempMap.put(FOURTH_COLUMN, com);
                                    tempMap.put(SIXTH_COLUMN,temp.getString("stage_comments"));

                                }

                                tempMap.put(FIFTH_COLUMN, temp.getString("completed"));
                                list.add(tempMap);
                            }   // end of for loop

                            ListViewAdapter adapter = new ListViewAdapter(AssetInformation.this, AssetInformation.this.list);
                            adapter.notifyDataSetChanged();

                            listView.setAdapter(adapter);

                            hidepDialog();

                        } catch (JSONException e) {
                            // do nothing for now
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // fill error handling code later
                hidepDialog();
            }
        });




        /* queue the requests into the volley queue */
        AppController.getInstance().addToRequestQueue(request1);
        AppController.getInstance().addToRequestQueue(request2);

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
