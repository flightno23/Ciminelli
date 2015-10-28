package com.example.girish.ciminelli;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private EditText editTextUserName;
    private EditText editTextPassword;

    public static final String USER_NAME = "USERNAME";

    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
            setContentView(R.layout.activity_main);

            /* get the username and password from the user */
            editTextUserName = (EditText) findViewById(R.id.editTextUserName);
            editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        } else {
            Intent intent = new Intent(this, SecondScreen.class);
            startActivity(intent);
            finish();
        }


    }

    /* method runs when the login button is selected */
    public void invokeLogin(View view){
        final String username = editTextUserName.getText().toString();
        final String password = editTextPassword.getText().toString();

        /* check if there is an internet connection available else display to user */
        if (haveNetworkConnection()) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    loginCheck(username, password);
                }
            });

        /* load the progress dialog box and show to the user */
            loadingDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Loading...");

        /* authenticate the user or else display invalid credentials */
            t.start();
        } else {
            Toast.makeText(this, "No Internet Connection. Please check device.", Toast.LENGTH_SHORT).show();
        }



    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }



    /* method that makes the post request to the server and checks the password
    * Runs from a worker thread and updates the UI as appropriate */
    private void loginCheck(String username, String password) {



        InputStream is = null;

        // username and password key value pairs for authentication
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        String result = null;

        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.drones.cse.buffalo.edu/ciminelli/login/login.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            is = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String s = result.trim();
        String message = "";
        try {

            JSONObject json = new JSONObject(s);
            message = json.getString("success");

            if (message.equalsIgnoreCase("1")) {
                JSONArray flag = json.getJSONArray("user_details");
                String verified = flag.getJSONObject(0).getString("verified");
                if (verified.equalsIgnoreCase("1")) {
                    SessionDetails.verified = true;
                } else {
                    SessionDetails.verified = false;
                }
            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        });

        if(message.equalsIgnoreCase("1")){
            Intent intent = new Intent(MainActivity.this, SecondScreen.class);

            SaveSharedPreference.setUserName(MainActivity.this, username);


            startActivity(intent);
            finish();

        } else {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Invalid User Name or Password", Toast.LENGTH_LONG).show();
                }
            });
        }

    }



}
