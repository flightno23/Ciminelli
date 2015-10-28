package com.example.girish.ciminelli;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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
import java.util.List;

/**
 * Created by girish on 10/27/15.
 */
public class ConnectAsset extends ActionBarActivity implements AdapterView.OnItemSelectedListener {


    private Spinner spinnerAsset, spinnerUnit;

    ArrayList<String> assetList;

    ArrayList<String> unitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_asset);

        spinnerAsset = (Spinner) findViewById(R.id.spinner_asset);
        spinnerUnit = (Spinner) findViewById(R.id.spinner_unit);



        assetList = new ArrayList<String>();
        unitList = new ArrayList<String>();
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
        final String sp1 = String.valueOf(spinnerAsset.getSelectedItem());

        Log.d("item:", sp1);

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                populateUnitSpinner(sp1);
            }
        });

        t2.start();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    public void populateUnitSpinner(String itemSelected) {
        itemSelected = itemSelected.replaceAll(" ", "+");
        String result = GET("http://www.drones.cse.buffalo.edu/ciminelli/connectasset/unitname.php?asset_name=" + itemSelected);

        Log.d("result::", result);
        unitList = new ArrayList<String>();

        try {

            JSONObject json = new JSONObject(result);
            JSONArray units = json.getJSONArray("units");
            JSONObject temp = null;

            for (int i = 0; i < units.length(); i++) {
                String unitNo = (String) units.get(i);

                unitList.add(unitNo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ArrayAdapter<String> data =
                        new ArrayAdapter<String>(ConnectAsset.this, android.R.layout.simple_spinner_dropdown_item, unitList);

                data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                data.notifyDataSetChanged();
                spinnerUnit.setAdapter(data);
            }
        });

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

        } catch (JSONException e) {
            e.printStackTrace();
        }



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerAsset.setOnItemSelectedListener(ConnectAsset.this);
                ArrayAdapter<String> dataAdapter =
                        new ArrayAdapter<String>(ConnectAsset.this, android.R.layout.simple_spinner_dropdown_item, assetList);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAsset.setAdapter(dataAdapter);

            }
        });

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

    private void makePost() {

        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost("http://www.drones.cse.buffalo.edu/ciminelli/connectasset/connect.php");

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("qr_code", SessionDetails.assetCode));
        nameValuePair.add(new BasicNameValuePair("unit_no", spinnerUnit.getSelectedItem().toString()));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // log exception
            e.printStackTrace();
        }

        //making POST request.
        try {
            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", response.toString());
        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }


    }


    public void connectAsset(View view) {
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                makePost();

                Intent intent = new Intent(ConnectAsset.this, AssetInformation.class);

                startActivity(intent);

                finish();
            }
        });

        t3.start();
    }
}
