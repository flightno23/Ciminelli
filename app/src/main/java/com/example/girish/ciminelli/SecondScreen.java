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
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by girish on 9/14/15.
 */
public class SecondScreen extends ActionBarActivity{

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    EditText assetCode;

    private Dialog loadingDialog;




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

    /* onClick handler for Getting Asset Information */
    public void findAsset(View view) {

        SessionDetails.unitNo = assetCode.getText().toString();

        if (SessionDetails.unitNo.equals("")) {
            Toast.makeText(this, "Invalid QR code. Please try again", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            //Toast.makeText(this,SessionDetails.unitNo, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this,SessionDetails.project_names, Toast.LENGTH_SHORT).show();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      loadingDialog = ProgressDialog.show(SecondScreen.this, "Please wait", "Loading...");
                  }
                });

                final String details = GET("http://www.drones.cse.buffalo.edu/ciminelli/qrcode/validateid.php?unit_no="+SessionDetails.unitNo);

                if (details.equals("success")) {
                    Intent intent = new Intent(SecondScreen.this, AssetInformation.class);
                    startActivity(intent);

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                    }
                });

            }
        });

        t.start();

        /* Intent intent = new Intent(this, AssetInformation.class);

        startActivity(intent); */

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
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");


                SessionDetails.assetCode = contents;

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog = ProgressDialog.show(SecondScreen.this, "Please wait", "Loading...");
                            }
                        });

                        final String details = GET("http://www.drones.cse.buffalo.edu/ciminelli/qrcode/validateqr.php?qr_code=" +  SessionDetails.assetCode);

                        if (details.equals("success")) {
                            Intent intent = new Intent(SecondScreen.this, AssetInformation.class);
                            startActivity(intent);

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                    Intent intent = new Intent(SecondScreen.this, ConnectAsset.class);
                                    startActivity(intent);
                                }
                            });
                        }

                    }
                });

                t.start();


            }
        }

    }
}
