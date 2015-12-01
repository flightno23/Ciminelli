package com.example.girish.ciminelli;

/**
 * Created by girish on 9/28/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


import static com.example.girish.ciminelli.SessionDetails.FIRST_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.FOURTH_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.SECOND_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.SIXTH_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.THIRD_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.FIFTH_COLUMN;

/**
 * Created by girish on 8/19/15.
 */
public class ListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView stageName;
    TextView stageComments;
    LinearLayout linear;
    TextView verified;
    TextView fullcomments;

    ImageButton clickListView;

    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {

        super();
        this.activity = activity;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();


        if(convertView == null){

            convertView=inflater.inflate(R.layout.adapter, null);

            stageName=(TextView) convertView.findViewById(R.id.name);
            stageComments=(TextView) convertView.findViewById(R.id.comments);

            linear = (LinearLayout) convertView.findViewById(R.id.linear);

            verified = (TextView) convertView.findViewById(R.id.verified);

            clickListView = (ImageButton) convertView.findViewById(R.id.click_button);

        }

        clickListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callStageScreen = new Intent(parent.getContext(), StageScreen.class);

                HashMap<String, String> tempExtras = list.get(position);

                callStageScreen.putExtra("unit_no", tempExtras.get(FIRST_COLUMN));
                callStageScreen.putExtra("stage_number", tempExtras.get(SECOND_COLUMN));
                callStageScreen.putExtra("stage_name", tempExtras.get(THIRD_COLUMN));
                callStageScreen.putExtra("stage_comments", tempExtras.get(FOURTH_COLUMN));
                callStageScreen.putExtra("completed", tempExtras.get(FIFTH_COLUMN));
                callStageScreen.putExtra("actual_comments",tempExtras.get(SIXTH_COLUMN));

                parent.getContext().startActivity(callStageScreen);
            }
        });


        HashMap<String, String> map=list.get(position);

        if (map.get(THIRD_COLUMN).equalsIgnoreCase("null")) {
            stageName.setText("No Stage Information.");
        } else {
            stageName.setText(map.get(THIRD_COLUMN));
            stageComments.setText(map.get(FOURTH_COLUMN));
        }


        if (map.get(FIFTH_COLUMN).equals("1")) {

            verified.setBackgroundColor(Color.YELLOW);

        } else if(map.get(FIFTH_COLUMN).equals("2")) {

            verified.setBackgroundColor(Color.GREEN);

        }



        return convertView;
    }



}
