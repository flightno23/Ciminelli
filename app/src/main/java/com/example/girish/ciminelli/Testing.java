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
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.util.Arrays;
import java.util.HashMap;

public class Testing extends AppCompatActivity implements OnClickListener, AdapterView.OnItemClickListener {


    ListView listView;
    String[] item;
    ArrayList<String> list;
    Dialog loadingDialog;
    String projectmname ="";
    ArrayAdapter<String> adapter;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        setContentView(R.layout.activity_testing);

        listView=(ListView) findViewById(R.id.listView);

        list=new ArrayList<>(Arrays.asList(projectmname));
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        /* Progress Dialog config */
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        /* render the adapter view with list of projects through volley */
        findProjects();

    }

    /*
     * Display list of projects to the user
     */
    private void findProjects() {
        showpDialog();

        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://www.drones.cse.buffalo.edu/ciminelli/assetscreen/get_project_details.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject json = new JSONObject(response);
                                JSONArray projectname = json.getJSONArray("projectName");
                                JSONObject temp = null;


                                for (int i=0;i<projectname.length();i++)
                                {
                                    temp=projectname.getJSONObject(i);
                                    String project = temp.getString("projectName");
                                    list.add(project);

                                    adapter.notifyDataSetChanged();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(Testing.this, "Some error with parsing.. Hit refresh and try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                            hidepDialog();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Testing.this, "Some error with network.. Hit refresh and try again",
                                    Toast.LENGTH_SHORT).show();
                            hidepDialog();
                        }
        });

        AppController.getInstance().addToRequestQueue(sr);
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SessionDetails.project_names= ((TextView)view).getText().toString();

        Log.d("Project Name: ", SessionDetails.project_names);

        Intent intent=new Intent(this,SecondScreen.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

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
