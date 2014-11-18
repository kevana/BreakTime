package com.breaktime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
