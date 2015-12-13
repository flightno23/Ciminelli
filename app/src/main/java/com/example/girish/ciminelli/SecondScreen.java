package com.example.girish.ciminelli;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


/**
 * Created by girish on 9/14/15.
 */
public class SecondScreen extends ActionBarActivity{

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    EditText assetCode;

    private Dialog loadingDialog;

    private ProgressDialog pDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen);
        LinearLayout layout_main=(LinearLayout) findViewById(R.id.action_bar_all);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        TextView action=(TextView) findViewById(R.id.textView_action);
        action.setText(SessionDetails.project_names);

        assetCode = (EditText) findViewById(R.id.asset_code);

        /* Progress Dialog config */
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

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

                SaveSharedPreference.clearUserName(SecondScreen.this);
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

    /*
     * Method that gets Asset Information by the unit number
     */
    public void getInfoUnitNumber(View view) {

        showpDialog();

        final String unit_number = assetCode.getText().toString();

        if (unit_number.equals("")) {
            Toast.makeText(this, "Invalid QR code. Please try again", Toast.LENGTH_SHORT).show();
            hidepDialog();
            return;
        } else {
            String url = "http://www.drones.cse.buffalo.edu/ciminelli/qrcode/validateid.php?unit_no=" + unit_number;
            StringRequest sr = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response:", response.trim().substring(0,3));
                            if (response.trim().equals("success")) {
                                Intent intent = new Intent(SecondScreen.this, AssetInformation.class);
                                SessionDetails.unitNo = unit_number;
                                startActivity(intent);
                            } else {
                                Toast.makeText(SecondScreen.this, "Invalid Unit No. Please try again..", Toast.LENGTH_SHORT).show();
                            }
                            hidepDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SecondScreen.this, "Some network Issue. Please try again", Toast.LENGTH_SHORT).show();
                    hidepDialog();
                }
            });
            AppController.getInstance().addToRequestQueue(sr);
        }
    }


    public void scanQR(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(SecondScreen.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                showpDialog();

                final String contents = intent.getStringExtra("SCAN_RESULT");

                String url = "http://www.drones.cse.buffalo.edu/ciminelli/qrcode/validateqr.php?qr_code=" +  contents;
                StringRequest sr = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if (!response.trim().equals("failure")) {

                                    SessionDetails.unitNo = response.trim();
                                    SessionDetails.assetCode = contents;

                                    Intent intent = new Intent(SecondScreen.this, AssetInformation.class);
                                    startActivity(intent);

                                } else {    // in case of failure

                                    SessionDetails.assetCode = contents;
                                    Intent intent = new Intent(SecondScreen.this, ConnectAsset.class);
                                    startActivity(intent);

                                }

                                hidepDialog();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SecondScreen.this, "Some network Issue. Please try again", Toast.LENGTH_SHORT).show();
                        hidepDialog();
                    }
                });
                AppController.getInstance().addToRequestQueue(sr);


            }
        }

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
