package com.example.girish.ciminelli;

import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;

import android.content.Intent;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private EditText editTextUserName;
    private EditText editTextPassword;

    private Button loginButton;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout_main=(LinearLayout) findViewById(R.id.action_bar_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        setContentView(R.layout.activity_main);

        /* get the username and password from the user */
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        loginButton = (Button) findViewById(R.id.button);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);



        /* On click listener for the login button */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


    }

    /*
     * Authenticates the user with the remote Web Server using Volley for fast
     * and error-free networking
     */
    private void login() {
        showpDialog();

        Log.d("Volley:", "in login method");

        final String username = editTextUserName.getText().toString();
        final String password = editTextPassword.getText().toString();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.drones.cse.buffalo.edu/ciminelli/login/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {   /* Successfull response listener */
                        try {
                            JSONObject json = new JSONObject(response);
                            String message = json.getString("success");

                            /* Authenticate user - success = 1 implies successfull authentication with remote server */
                            if (message.equalsIgnoreCase("1")) {
                                JSONArray flag = json.getJSONArray("user_details");
                                String verified = flag.getJSONObject(0).getString("verified");

                                if (verified.equalsIgnoreCase("1")) {
                                    SessionDetails.verified = true;
                                } else {
                                    SessionDetails.verified = false;
                                }

                                SaveSharedPreference.setUserName(getApplicationContext(), username);
                                Log.d("Volley:", username);
                                Log.d("Verified", String.valueOf(SessionDetails.verified));

                                Intent intent = new Intent(MainActivity.this, Testing.class);

                                startActivity(intent);


                            } else {
                                Toast.makeText(MainActivity.this, "Invalid Username or Password..", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Some Error with parsing. Try again", Toast.LENGTH_SHORT).show();
                        }

                        hidepDialog();

                    }
                }, new Response.ErrorListener() {   /* Error Listener (Bad network connection, etc) */
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidepDialog();
                        Toast.makeText(MainActivity.this, "Some Problem with network....", Toast.LENGTH_LONG).show();

                        Log.d("Volley:", error.toString());
                    }
        }) {
            /* POST parameters added to this http request */
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                /* Pass in the POST parameters */
                params.put("username", username);
                params.put("password", password);

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
