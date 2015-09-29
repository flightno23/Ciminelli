package com.example.girish.ciminelli;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import static com.example.girish.ciminelli.SessionDetails.THIRD_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.FIFTH_COLUMN;

/**
 * Created by girish on 9/28/15.
 */
public class AssetInformation extends ActionBarActivity {

    private Dialog loadingDialog;

    private ArrayList<HashMap<String, String>> list;

    ListView listView;

    TextView qrCode, unitNumber, area, location, service, overallComments, assetName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asset_screen);

        /*Initializing all the text boxes*/
        qrCode = (TextView) findViewById(R.id.qr_code);
        unitNumber = (TextView) findViewById(R.id.unit_no);
        area = (TextView) findViewById(R.id.area);
        location = (TextView) findViewById(R.id.location);
        service = (TextView) findViewById(R.id.service);
        overallComments = (TextView) findViewById(R.id.overall_comments);
        assetName = (TextView) findViewById(R.id.asset_name);
        listView = (ListView) findViewById(R.id.listView1);



        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                renderView();
            }
        });

        t.start();


        // On click listener for the stages
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent callStageScreen = new Intent(AssetInformation.this, StageScreen.class);

                HashMap<String, String> tempExtras = list.get(position);

                callStageScreen.putExtra("qr_code", tempExtras.get(FIRST_COLUMN));
                callStageScreen.putExtra("stage_number", tempExtras.get(SECOND_COLUMN));
                callStageScreen.putExtra("stage_name", tempExtras.get(THIRD_COLUMN));
                callStageScreen.putExtra("stage_comments", tempExtras.get(FOURTH_COLUMN));
                callStageScreen.putExtra("completed", tempExtras.get(FIFTH_COLUMN));

                startActivity(callStageScreen);

            }
        });

    }

    /* method does the GET requests to the server and populated the hashmap for the list view adapter*/
    private void renderView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = ProgressDialog.show(AssetInformation.this, "Please wait", "Loading...");
            }
        });

        final String stageDetails = GET(SessionDetails.ip + "android-connect/get_stage_details.php?qr_code="+ SessionDetails.assetCode);
        final String assetDetails = GET(SessionDetails.ip + "android-connect/get_asset_details.php?qr_code="+ SessionDetails.assetCode);

        list = new ArrayList<HashMap<String, String>>();

        // ----------------

        final String unit_no;
        final String asset_name;
        final String overall_comments;
        final String area_asset;
        final String location_asset;
        final String service_asset;

        try{

            JSONObject json = new JSONObject(stageDetails);
            JSONArray stages = json.getJSONArray("stages");
            JSONObject temp = null;



            for (int i=0; i < stages.length(); i++) {

                temp = stages.getJSONObject(i);
                HashMap<String, String> tempMap = new HashMap<String, String>();
                tempMap.put(FIRST_COLUMN, temp.getString("qr_code"));
                tempMap.put(SECOND_COLUMN, temp.getString("stage_number"));
                tempMap.put(THIRD_COLUMN, temp.getString("stage_name"));
                tempMap.put(FOURTH_COLUMN, temp.getString("stage_comments"));
                tempMap.put(FIFTH_COLUMN, temp.getString("completed"));
                list.add(tempMap);

            }

            json = new JSONObject(assetDetails);
            JSONArray assets = json.getJSONArray("assets");
            temp = assets.getJSONObject(0);

            unit_no = temp.getString("unit_no");
            asset_name = temp.getString("asset_name");
            overall_comments = temp.getString("overall_comments");
            area_asset = temp.getString("area");
            location_asset = temp.getString("location");
            service_asset = temp.getString("service");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    unitNumber.setText("Unit No: " + unit_no);
                    qrCode.setText("Asset QR Code No: " + SessionDetails.assetCode);
                    assetName.setText("Asset Name: " + asset_name);
                    overallComments.setText("Overall Comments: " + overall_comments);
                    area.setText("Area: " + area_asset);
                    location.setText("Location: " + location_asset);
                    service.setText("Service: " + service_asset);

                    ListViewAdapter adapter = new ListViewAdapter(AssetInformation.this, AssetInformation.this.list);
                    adapter.notifyDataSetChanged();

                    listView.setAdapter(adapter);
                    loadingDialog.dismiss();

                }
            });

        } catch (JSONException e) {

            e.printStackTrace();
        }

        // --------------------




    }

    /* method performs a GET request to the given url */
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


    /* method called by the GET method to read the response from web server */
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
