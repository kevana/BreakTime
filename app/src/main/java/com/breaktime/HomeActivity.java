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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
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



        // SET UP PREFERENCE MANAGER
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

        // SET UP GLOBAL INTENT LIST
        Globals g = Globals.getInstance();
        if (g.getData().isEmpty()){
            Resources res = getResources();
            settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String[] inital_items = res.getStringArray(R.array.activity_choose_break_options_array);
            String[] static_items = res.getStringArray(R.array.activity_choose_break_static);
            Set<String> activities_names = settings.getStringSet(PrefID.ACTIVITIES, null);
            HashMap<String,Intent> intents = new HashMap<String,Intent>();

            // Redo all intents for saved activities (super hacky whatever)
            Set<String> activity_names_and_packages = settings.getStringSet(PrefID.INSTALLED_APPS_WITH_PACKAGE, null);

            if (activity_names_and_packages != null) {
                for (String appNamePack : activity_names_and_packages){
                    String[] parts = appNamePack.split(";");
                    if(activities_names.contains(parts[0])){
                        //Contains app name, add intent
                        Intent tempIntent = getPackageManager().getLaunchIntentForPackage(parts[1]);
                        intents.put(parts[0], tempIntent);
                    }
                }
            }
            // Create home intent
            for (String activ : inital_items){
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intents.put(activ, homeIntent);
            }
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intents.put(static_items[0], homeIntent);
            Intent intent = new Intent(this, StudyTimerActivity.class);
            intents.put(static_items[1], intent);
            g.setData(intents);
        }
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

    public void gettingStarted(View view) {
        Intent intent = new Intent(this, GettingStartedActivity.class);
        startActivity(intent);
    }
}
