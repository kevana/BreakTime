package com.breaktime;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class SettingsActivity extends Activity {
    private TextView studyTimeText;
    private TextView breakTimeText;
    private long studyTime;
    private long breakTime;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.v("SettingsActivity", "Preferences retrieved: " + settings.toString());
        studyTime = settings.getLong(PrefID.STUDY_TIME, -1);
        breakTime = settings.getLong(PrefID.BREAK_TIME, -1);
        if (studyTime < 0) {
            studyTime = 30000L;
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.STUDY_TIME, studyTime);
            ed.apply();
        }
        if (breakTime < 0) {
            breakTime = 5000L;
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.BREAK_TIME, breakTime);
            ed.apply();
        }
        studyTimeText = (TextView) findViewById(R.id.studyTimeText);
        studyTimeText.setText(String.format("%d", studyTime / 1000));
        breakTimeText = (TextView) findViewById(R.id.breakTimeText);
        breakTimeText.setText(String.format("%d", breakTime / 1000));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
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

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = settings.edit();
        ed.putLong(PrefID.STUDY_TIME, studyTime);
        ed.putLong(PrefID.BREAK_TIME, breakTime);
        ed.apply();
    }

    //TODO: Make all time increments into minutes, instead of seconds
    public void incrementStudyTime(View v) {
        if (studyTime >= 100000L) {
            return; //TODO Feedback?
        } else {
            studyTime += 5000L;
            studyTimeText.setText(String.format("%d", studyTime / 1000));
        }
    }

    public void decrementStudyTime(View v) {
        if (studyTime <= 10000L) {
            return; //TODO Feedback?
        } else {
            studyTime -= 5000L;
            studyTimeText.setText(String.format("%d", studyTime / 1000));
        }
    }

    public void incrementBreakTime(View v) {
        if (breakTime >= 10000L) {
            return;
        }

        breakTime += 1000L;
        breakTimeText.setText(String.format("%d", breakTime / 1000));
    }

    public void decrementBreakTime(View v) {

        if (breakTime <= 1000L) {
            return;
        }

        breakTime -= 1000L;
        breakTimeText.setText(String.format("%d", breakTime / 1000));
    }

}
