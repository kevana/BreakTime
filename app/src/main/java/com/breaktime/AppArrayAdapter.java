package com.breaktime;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Patrick on 11/30/2014.
 */
public class AppArrayAdapter extends ArrayAdapter<String>{
    public AppArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position,convertView, parent);
        //TODO add remove to each entry
        return view;
    }
}
