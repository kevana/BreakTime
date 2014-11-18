package com.breaktime;
import java.util.List;
import java.util.Arrays;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.TypedValue;
import android.util.Log;


public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

    private List<Integer> images;
    private static final String TAG = "ArrayAdapterTest";

    public ArrayAdapterWithIcon(Context context, List<String> items, List<Integer> images) {
        super(context, android.R.layout.select_dialog_item, items);
        this.images = images;
    }

    public ArrayAdapterWithIcon(Context context, String[] items, Integer[] images) {
        super(context, android.R.layout.select_dialog_item, items);
        this.images = Arrays.asList(images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        Log.d(TAG, "position :" + images.get(position));
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); //#TODO Add the image thing (images.get(position), 0, 0, 0)
        textView.setCompoundDrawablePadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
        return view;
    }

}