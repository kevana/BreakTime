package com.breaktime;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import io.fabric.sdk.android.Fabric;



public class HomeActivity extends Activity {

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long studyTime = settings.getLong(PrefID.STUDY_TIME, -1);
        long breakTime = settings.getLong(PrefID.BREAK_TIME, -1);
        if(studyTime < 0){
            studyTime = 30000L;
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.STUDY_TIME, studyTime);
            ed.commit();
        }
        if(breakTime < 0){
            breakTime = 5000L;
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.BREAK_TIME, breakTime);
            ed.commit();
        }
        Set<String> activities = new HashSet<String>();
        activities = settings.getStringSet(PrefID.ACTIVITIES, null);
        if(activities == null){
            SharedPreferences.Editor ed = settings.edit();
            Resources res = getResources();
            String[] items = res.getStringArray(R.array.activity_choose_break_options_array);
            activities = new HashSet<String>(Arrays.asList(items));
            ed.putStringSet(PrefID.ACTIVITIES, activities);
            ed.commit();
            Log.v("SettingsActivity", "Preferences created: " + activities.toString());
        }
        Log.v("SettingsActivity", "Preferences retrieved: " + settings.getStringSet(PrefID.ACTIVITIES, null).toString());
    }

    public void startStudying(View view) {
        SharedPreferences.Editor ed = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext()).edit();
        ed.putLong(PrefID.STUDY_TIME_REMAINING, -1);
        ed.apply();
        Intent intent = new Intent(this, StudyTimerActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
