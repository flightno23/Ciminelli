package com.example.girish.ciminelli;

import android.content.pm.PackageInstaller;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by girish on 9/28/15.
 */
public class StageScreen extends ActionBarActivity{


    EditText viewComment;

    Button stageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LinearLayout layout_main=(LinearLayout) findViewById(R.id.action_bar_all);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        TextView action=(TextView) findViewById(R.id.textView_action);
        action.setText(SessionDetails.project_names);
        setContentView(R.layout.stage_screen);
        Intent i= getIntent();
        String comments = i.getStringExtra("actual_comments");


        viewComment = (EditText) findViewById(R.id.view_comment);
        viewComment.setText(comments);

        stageButton = (Button) findViewById(R.id.stage_button);

        if (SessionDetails.verified) {

            stageButton.setText("Mark Stage as Verified");

        } else {

            stageButton.setText("Mark Stage as Tested");

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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


                SessionDetails.assetCode = "";


                Intent intent = new Intent(StageScreen.this, MainActivity.class);

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


    /* make a post request with parameters as (URL, NameValuePair) */
    private void makePostRequest(String url, List<NameValuePair> nameValuePair) {
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost("http://www.drones.cse.buffalo.edu/ciminelli/stagescreen/" + url);


        //Encoding POST data
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


    /* Method that runs when the save comment button is selected */
    public void saveComment(View view) {

        // final String unitNo = getIntent().getExtras().getString("unit_no");
        final String stageNumber = getIntent().getExtras().getString("stage_number");
        final String comments = viewComment.getText().toString();

        /* check if user has permissions to save the comment */
        /*if (Constants.comments == false) {
            Toast.makeText(this, "You don't have permissions to save comments", Toast.LENGTH_LONG).show();
            return;
        } */

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
                nameValuePair.add(new BasicNameValuePair("unit_no", SessionDetails.unitNo));
                nameValuePair.add(new BasicNameValuePair("stage_number", stageNumber));
                nameValuePair.add(new BasicNameValuePair("stage_comments", comments));
                makePostRequest("update_stage_comment.php", nameValuePair);

                Intent newIntent = new Intent(StageScreen.this, AssetInformation.class);
                startActivity(newIntent);
                finish();
            }
        });

        t.start();

    }

    /* Mark Stage method */
    public void markStage(View view) {

        final String stageNumber = getIntent().getExtras().getString("stage_number");


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
                nameValuePair.add(new BasicNameValuePair("unit_no", SessionDetails.unitNo));
                nameValuePair.add(new BasicNameValuePair("stage_number", stageNumber));

                if (SessionDetails.verified) {
                    nameValuePair.add(new BasicNameValuePair("completed", "2"));
                } else {
                    nameValuePair.add(new BasicNameValuePair("completed", "1"));
                }
                
                makePostRequest("update_stage.php", nameValuePair);

                Intent newIntent = new Intent(StageScreen.this, AssetInformation.class);
                startActivity(newIntent);
                finish();
            }
        });

        t.start();

    }
}
