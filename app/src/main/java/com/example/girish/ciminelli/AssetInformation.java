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
    TextView qrCode, unitNumber, location, service, assetName;

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

        /*Initializing all the text boxes*/
       // qrCode = (TextView) findViewById(R.id.qr_code);
        unitNumber = (TextView) findViewById(R.id.unit_no);
        location = (TextView) findViewById(R.id.location);
       // service = (TextView) findViewById(R.id.service);
        assetName = (TextView) findViewById(R.id.asset_name);
        listView = (ListView) findViewById(R.id.listView1);



        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                renderView();
            }
        });

        t.start();



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



    /* method does the GET requests to the server and populated the hashmap for the list view adapter*/
    private void renderView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = ProgressDialog.show(AssetInformation.this, "Please wait", "Loading...");
            }
        });

        final String stageDetails = GET("http://www.drones.cse.buffalo.edu/ciminelli/assetscreen/get_stage_details.php?unit_no="+SessionDetails.unitNo+"&"+"project_name="+SessionDetails.project_names);
        final String assetDetails = GET("http://www.drones.cse.buffalo.edu/ciminelli/assetscreen/get_asset_details.php?unit_no="+SessionDetails.unitNo+"&"+"project_name="+SessionDetails.project_names);

        Log.d("stages", stageDetails);
        Log.d("assets", assetDetails);

        list = new ArrayList<HashMap<String, String>>();

        // ----------------

        final String unit_no;
        final String asset_name;
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

            }

            json = new JSONObject(assetDetails);
            JSONArray assets = json.getJSONArray("assets");
            temp = assets.getJSONObject(0);

            unit_no = temp.getString("unit_no");
            asset_name = temp.getString("asset_name");
            location_asset = temp.getString("location");
           // service_asset = temp.getString("manufacturer");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    unitNumber.setText( unit_no);
                    //qrCode.setText("Asset QR Code No: " + SessionDetails.assetCode);
                    assetName.setText( asset_name);
                    location.setText( location_asset);
                    //service.setText("Manufacturer: " + service_asset);

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
