package com.breaktime;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 11/30/2014.
 */
public class AppArrayAdapter extends ArrayAdapter<String>{
    public AppArrayAdapter(Context context, List<String> objects) {
        super(context, android.R.layout.select_dialog_item, objects);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position,convertView, parent);
        //TODO add remove to each entry
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setTextColor(Color.WHITE);
        return view;
    }

}
