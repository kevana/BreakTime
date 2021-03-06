package com.breaktime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.content.res.Resources;


public class ChooseBreakActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private SharedPreferences settings;
    private Vibrator vibrator;
    final int min = 0;
    final private  String TAG = "ChooseBreak-debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_break);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100L, 200L, 100L, 200L, 100L, 200L};
        vibrator.vibrate(pattern, -1);
        FlashController.flash(200);


        Resources res = getResources();
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> activities = settings.getStringSet(PrefID.ACTIVITIES, null);
        String[] static_items = res.getStringArray(R.array.activity_choose_break_static);
        List<String> final_items = new ArrayList<String>();
        String[] items = activities.toArray(new String[activities.size()]);
        Random rand = new Random();
        int max = items.length;
        int first = -1;
        if(max > 0){
            first = rand.nextInt(max - min) + min;
            final_items.add(items[first]);
        }
        if(max > 1) {
            int second = rand.nextInt(max - min) + min;
            while (second == first) {
                second = rand.nextInt(max - min) + min;
            }
            final_items.add(items[second]);
        }


        for (String item : static_items){
            final_items.add(item);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, final_items);
        listView = (ListView) findViewById(R.id.chooseBreakListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Item got : " + adapter.getItemAtPosition(position));
        // get Intent to start
        Globals g = Globals.getInstance();
        HashMap<String, Intent> intents = g.getData();
        Log.d(TAG, "Intents first : " + intents.entrySet());
        Intent startMe = intents.get(adapter.getItemAtPosition(position));
        Log.d(TAG, "Intent got : " + startMe);
        g.setActivity(startMe);
        // Start App
        Resources res = getResources();
        String[] static_items = res.getStringArray(R.array.activity_choose_break_static);
        startActivity(startMe);
        if (!adapter.getItemAtPosition(position).equals(static_items[1])){
            startService(new Intent(this, BreakTimerService.class));
        }
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void gotoWork(View view) {
        Intent intent = new Intent(this, BackToWorkActivity.class);
        startActivity(intent);
    }

    public void pullAppList(View view){
        ArrayList<String> currentApps = new ArrayList<String>();
        currentApps.addAll(settings.getStringSet(PrefID.ACTIVITIES, null));

        AppArrayAdapter appListAdapter = new AppArrayAdapter(this, currentApps);
        AppListManager.getInstance().pullAppList(view, getPackageManager(), this, currentApps, appListAdapter);
    }
}
