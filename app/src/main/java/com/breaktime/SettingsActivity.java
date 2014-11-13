package com.breaktime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SettingsActivity extends Activity {
    private TextView studyTimeText;
    private long studyTime = 30000L;
    private long breakTime = 5000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

    //TODO: Make all time increments into minutes, instead of seconds
    private void incrementStudyTime()
    {
        studyTime += 5000L;
        studyTimeText.setText(String.format("%d", studyTime/1000));
    }
    private void decrementStudyTime() {
        studyTime -= 5000L;
        studyTimeText.setText(String.format("%d", studyTime/1000));
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
