package com.example.girish.ciminelli;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by girish on 10/27/15.
 */
public class ConnectAsset extends ActionBarActivity implements AdapterView.OnItemSelectedListener {


    private Spinner spinnerAsset, spinnerUnit;

    ArrayList<String> assetList;

    ArrayList<String> unitList;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_asset);
        LinearLayout layout_main=(LinearLayout) findViewById(R.id.action_bar_all);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        TextView action=(TextView) findViewById(R.id.textView_action);
        action.setText(SessionDetails.project_names);

        spinnerAsset = (Spinner) findViewById(R.id.spinner_asset);
        spinnerUnit = (Spinner) findViewById(R.id.spinner_unit);

        /* Progress Dialog config */
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        /* Declare array list that hold the distinct elements of each spinner */
        assetList = new ArrayList<String>();
        unitList = new ArrayList<String>();

        // make a request to populate Asset Spinner
        populateAssetSpinner();


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // do nothing
        final String sp1 = String.valueOf(spinnerAsset.getSelectedItem());

        Log.d("item:", sp1);

        populateUnitSpinner(sp1);


        /*
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                populateUnitSpinner(sp1);
            }
        });

        t2.start();
        */

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    public void populateUnitSpinner(String itemSelected) {

        showpDialog();

        itemSelected = itemSelected.replaceAll(" ", "+");
        String unitUrl = "http://www.drones.cse.buffalo.edu/ciminelli/connectasset/unitname.php?asset_name=" + itemSelected;

        unitList = new ArrayList<String>();

        StringRequest sr = new StringRequest(Request.Method.GET, unitUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray units = json.getJSONArray("units");


                            for (int i = 0; i < units.length(); i++) {
                                String unitNo = (String) units.get(i);

                                unitList.add(unitNo);
                            }

                            ArrayAdapter<String> data =
                                    new ArrayAdapter<String>(ConnectAsset.this, android.R.layout.simple_spinner_dropdown_item, unitList);

                            data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            data.notifyDataSetChanged();
                            spinnerUnit.setAdapter(data);

                        } catch(JSONException e) {
                            // fill in the error handling code later
                        }
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ConnectAsset.this, "Some network Issue. Please try again", Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(sr);



    }


    /* method to populate the asset spinner */
    public void populateAssetSpinner() {

        showpDialog();

        String spinnerUrl = "http://www.drones.cse.buffalo.edu/ciminelli/connectasset/assetname.php";

        StringRequest sr = new StringRequest(Request.Method.GET, spinnerUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray stages = json.getJSONArray("assets");
                            JSONObject temp = null;

                            for (int i = 0; i < stages.length(); i++) {
                                String assetName = (String) stages.get(i);

                                assetList.add(assetName);
                            }

                            spinnerAsset.setOnItemSelectedListener(ConnectAsset.this);
                            ArrayAdapter<String> dataAdapter =
                                    new ArrayAdapter<String>(ConnectAsset.this, android.R.layout.simple_spinner_dropdown_item, assetList);

                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAsset.setAdapter(dataAdapter);

                        } catch (JSONException e) {
                            // fill code for error handling part
                        }
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ConnectAsset.this, "Some network Issue. Please try again", Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(sr);



    }


    /*
     *  Connect asset button that connects a QR code to a particular asset
     */
    public void connectAsset(View view) {

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.drones.cse.buffalo.edu/ciminelli/connectasset/connect.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {   /* Successfull response listener */

                        SessionDetails.unitNo = spinnerUnit.getSelectedItem().toString();

                        Intent intent = new Intent(ConnectAsset.this, AssetInformation.class);

                        startActivity(intent);

                        hidepDialog();

                    }
                }, new Response.ErrorListener() {   /* Error Listener (Bad network connection, etc) */
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Toast.makeText(ConnectAsset.this, "Some Problem with network....", Toast.LENGTH_LONG).show();

            }
        }) {
            /* POST parameters added to this http request */
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                /* Pass in the POST parameters */
                params.put("qr_code", SessionDetails.assetCode);
                params.put("unit_no", spinnerUnit.getSelectedItem().toString());

                return params;

            }
        };

        /* Add this request to the volley queue */
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
