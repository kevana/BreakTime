package com.breaktime;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.prefs.Preferences;


public class SettingsActivity extends Activity {
    private TextView studyTimeText;
    private long studyTime;
    private long breakTime;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = getPreferences(MODE_PRIVATE);
        studyTime = settings.getLong(PrefID.STUDY_TIME, -1);
        breakTime = settings.getLong(PrefID.BREAK_TIME, -1);
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
        studyTimeText = (TextView) findViewById(R.id.studyTimeText);
        studyTimeText.setText(String.format("%d", studyTime/1000));
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
    protected void onPause()
    {
        super.onPause();

        SharedPreferences.Editor ed = settings.edit();
        ed.putLong(PrefID.STUDY_TIME, studyTime);
        ed.putLong(PrefID.BREAK_TIME, breakTime);
        ed.commit();
    }

    //TODO: Make all time increments into minutes, instead of seconds
    public void incrementStudyTime(View v)
    {
        if(studyTime >= 100000L){
            return; //TODO Feedback?
        }
        else {
            studyTime += 5000L;
            studyTimeText.setText(String.format("%d", studyTime / 1000));
        }
    }
    public void decrementStudyTime(View v) {
        if(studyTime <= 10000L){
            return; //TODO Feedback?
        }
        else {
            studyTime -= 5000L;
            studyTimeText.setText(String.format("%d", studyTime / 1000));
        }
    }

    private void incrementBreakTime()
    {
        breakTime += 1000L;
    }
    private void decrementBreakTime() {
        breakTime -= 1000L;
    }

    public long getStudyTime() {
        return studyTime;
    }
    public long getBreakTime() {
        return breakTime;
    }

}
