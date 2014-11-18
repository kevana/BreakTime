package com.breaktime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startStudying(View view) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        ed.putLong(PrefID.STUDY_TIME_REMAINING, -1);
        ed.commit();
        Intent intent = new Intent(this, StudyTimerActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
