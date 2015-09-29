package com.example.girish.ciminelli;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
        setContentView(R.layout.activity_main);

        /* get the username and password from the user */
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

    }

    /* method runs when the login button is selected */
    public void invokeLogin(View view){
        final String username = editTextUserName.getText().toString();
        final String password = editTextPassword.getText().toString();

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
            HttpPost httpPost = new HttpPost(SessionDetails.ip + "ciminelli/login.php");
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        });

        if(s.equalsIgnoreCase("success")){
            Intent intent = new Intent(MainActivity.this, SecondScreen.class);
            SessionDetails.username = username;
            SessionDetails.password = password;

            // finish(); // dont finish this activity
            startActivity(intent);
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Invalid User Name or Password", Toast.LENGTH_LONG).show();
                }
            });

        }

    }



}
