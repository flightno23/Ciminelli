package com.example.girish.ciminelli;

/**
 * Created by girish on 9/28/15.
 */

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


import static com.example.girish.ciminelli.SessionDetails.FIRST_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.FOURTH_COLUMN;
import static com.example.girish.ciminelli.SessionDetails.SECOND_COLUMN;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();


        if(convertView == null){

            convertView=inflater.inflate(R.layout.adapter, null);

            stageName=(TextView) convertView.findViewById(R.id.name);
            stageComments=(TextView) convertView.findViewById(R.id.comments);

            linear = (LinearLayout) convertView.findViewById(R.id.linear);

        }

        HashMap<String, String> map=list.get(position);
        stageName.setText(map.get(THIRD_COLUMN));
        stageComments.setText(map.get(FOURTH_COLUMN));

        if (map.get(FIFTH_COLUMN).equals("1")) {

            linear.setBackgroundColor(Color.YELLOW);

        }

        return convertView;
    }
}
