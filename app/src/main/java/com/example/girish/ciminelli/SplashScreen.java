package com.example.girish.ciminelli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by girish on 9/28/15.
 */
public class SplashScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        /* thread that holds the splash screen image for a particular amount of time */
        Thread timerThread = new Thread(){
            public void run(){
                try{

                    sleep(2000);

                }catch(InterruptedException e){

                    e.printStackTrace();

                }finally{

                    if (SaveSharedPreference.getUserName(getApplicationContext()).length() == 0
                            || (!Utility.haveNetworkConnection(getApplicationContext())) ) {

                        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Intent intent = new Intent(SplashScreen.this,Testing.class);
                        startActivity(intent);
                        finish();

                    }

                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
