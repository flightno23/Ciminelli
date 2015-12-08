package com.example.girish.ciminelli;

import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

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
import java.util.Arrays;
import java.util.HashMap;

public class Testing extends AppCompatActivity implements OnClickListener, AdapterView.OnItemClickListener {
ListView listView;


    String[] item;
    ArrayList<String> list;
    Dialog loadingDialog;
    String projectmname ="";
    ArrayAdapter<String> adapter;



   // private ArrayList<HashMap<String, String>> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LinearLayout layout_main=(LinearLayout) findViewById(R.id.action_bar_all);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);




        setContentView(R.layout.activity_testing);

        listView=(ListView) findViewById(R.id.listView);





        list=new ArrayList<>(Arrays.asList(projectmname));
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);



        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                renderView();
            }
        });

        t.start();
    }

    private void renderView() {
      //  text.setText("hio");

        //
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = ProgressDialog.show(Testing.this, "Please wait", "Loading...");
            }
        });
        //list = new ArrayList<HashMap<String, String>>();
        final String projectDetails = GET("http://www.drones.cse.buffalo.edu/ciminelli/assetscreen/get_project_details.php");






        try{
            JSONObject json = new JSONObject(projectDetails);
            JSONArray projectname = json.getJSONArray("projectName");
            JSONObject temp = null;
            //projectmname = temp.getString("projectName");

              //item=projectmname[0];
                //    text.setText(projectmname);

            for (int i=0;i<projectname.length();i++)
            {
                temp=projectname.getJSONObject(i);
                String project = (String) temp.getString("projectName");
                list.add(project);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                //list.add(unitNo);

               // SessionDetails.project_name=unitNo;
            }



            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    loadingDialog.dismiss();

                }
            });


        }
        catch (JSONException e) {
            e.printStackTrace();
        }

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

                SaveSharedPreference.clearUserName(Testing.this);
                SessionDetails.assetCode = "";

                Intent intent = new Intent(Testing.this, MainActivity.class);

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




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SessionDetails.project_names= ((TextView)view).getText().toString();

        Intent intent=new Intent(this,SecondScreen.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}
