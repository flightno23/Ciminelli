package com.example.girish.ciminelli;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by girish on 9/14/15.
 */
public class SecondScreen extends ActionBarActivity{

    EditText assetCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen);

        assetCode = (EditText) findViewById(R.id.asset_code);

        /* MainActivity is the parent of SecondScreen class */
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

                SessionDetails.password = "";
                SessionDetails.username = "";
                SessionDetails.assetCode = "";


                Intent intent = new Intent(SecondScreen.this, MainActivity.class);

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

    /* onClick handler for Getting Asset Information */
    public void findAsset(View view) {

        SessionDetails.assetCode = assetCode.getText().toString();

        Log.d("mess", SessionDetails.assetCode);

        Intent intent = new Intent(this, AssetInformation.class);

        startActivity(intent);

    }
}
