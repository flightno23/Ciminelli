package com.example.girish.ciminelli;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * Created by girish on 9/28/15.
 */
public class StageScreen extends ActionBarActivity{

    TextView welcome;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stage_screen);

        welcome = (TextView) findViewById(R.id.textBox);


    }
}
