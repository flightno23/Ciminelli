package com.example.girish.ciminelli;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

/**
 * Created by girish on 10/27/15.
 */
public class ConnectAsset extends ActionBarActivity implements AdapterView.OnItemSelectedListener {


    private Spinner spinnerAsset, spinnerUnit;

    ArrayList<String> assetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_asset);

        spinnerAsset = (Spinner) findViewById(R.id.spinner_asset);
        spinnerUnit = (Spinner) findViewById(R.id.spinner_unit);

        assetList = new ArrayList<String>();
        // make a request to populate spinner 1

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                populateAssetSpinner();
            }
        });

        t.start();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // do nothing
        String sp1 = String.valueOf(spinnerAsset.getSelectedItem());


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    public void populateUnitSpinner(String itemSelected) {
        String result = GET("http://www.drones.cse.buffalo.edu/ciminelli/unitname.php" + itemSelected);

        
    }


    /* method to populate the asset spinner */
    public void populateAssetSpinner() {
        String result = GET("http://www.drones.cse.buffalo.edu/ciminelli/connectasset/assetname.php");

        try {
            JSONObject json = new JSONObject(result);
            JSONArray stages = json.getJSONArray("assets");
            JSONObject temp = null;

            for (int i = 0; i < stages.length(); i++) {
                String assetName = (String) stages.get(i);

                assetList.add(assetName);
            }


            ArrayAdapter<String> dataAdapter =
                    new ArrayAdapter<String>(ConnectAsset.this, android.R.layout.simple_spinner_dropdown_item, assetList);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAsset.setAdapter(dataAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
