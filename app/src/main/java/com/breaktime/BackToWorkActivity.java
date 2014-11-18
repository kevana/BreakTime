package com.breaktime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;


public class BackToWorkActivity extends Activity {

    private Vibrator vibrator;

    boolean leavingByButtonPush = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_to_work);
        stopService(new Intent(this, BreakTimerService.class));

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100L, 200L,100L, 200L,100L, 200L};
        vibrator.vibrate(pattern, -1);
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
