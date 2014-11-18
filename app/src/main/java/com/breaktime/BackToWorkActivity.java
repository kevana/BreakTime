package com.breaktime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class BackToWorkActivity extends Activity {

    boolean leavingByButtonPush = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_to_work);
        stopService(new Intent(this, BreakTimerService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_to_work, menu);
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
    public void onStop(){
        if(!leavingByButtonPush) {
            snooze(getWindow().getDecorView().findViewById(R.id.imageButton2));
        }
        super.onStop();

    }


    public void openSettings(View view) {
        leavingByButtonPush = true;
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startStudying(View view) {
        leavingByButtonPush = true;
        Intent intent = new Intent(this, StudyTimerActivity.class);
        startActivity(intent);
    }

    public void snooze(View view) {
        Toast.makeText(getApplicationContext(), view.getContentDescription(),
                Toast.LENGTH_SHORT).show();
        startService(new Intent(this, BreakTimerService.class));
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
}
