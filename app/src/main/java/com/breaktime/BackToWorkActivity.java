package com.breaktime;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import com.breaktime.screen.ScreenReceiver;


public class BackToWorkActivity extends Activity {

    private Vibrator vibrator;

    boolean leavingByButtonPush = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_to_work);


        //Set up ScreenReceiver.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1300L, 200L,100L, 200L,100L, 200L};
        vibrator.vibrate(pattern, 0);
    }

    @Override
    public void onResume(){
        stopService(new Intent(this, BreakTimerService.class));
        super.onResume();
    }

    @Override
    public void onPause(){
        vibrator.cancel();
        if(!leavingByButtonPush
                && ScreenReceiver.wasScreenOn
                && !BreakTimerService.serviceRunning) {
            View current = getWindow().getDecorView().findViewById(R.id.imageButton2);
            Toast.makeText(getApplicationContext(), current.getContentDescription(),
                    Toast.LENGTH_SHORT).show();
            startService(new Intent(this, BreakTimerService.class));
        }
        super.onPause();

    }


    public void openSettings(View view) {
        leavingByButtonPush = true;
        //Cancel in case it's running:
        stopService(new Intent(this, BreakTimerService.class));
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startStudying(View view) {
        leavingByButtonPush = true;
        //Cancel in case it's running:
        stopService(new Intent(this, BreakTimerService.class));
        Intent intent = new Intent(this, StudyTimerActivity.class);
        startActivity(intent);
    }

    public void snooze(View view) {
        Toast.makeText(getApplicationContext(), view.getContentDescription(),
                Toast.LENGTH_SHORT).show();
        startService(new Intent(this, BreakTimerService.class));
        // Get current Activity
        Globals g = Globals.getInstance();
        Intent currentActivity = g.getCurrentActivity();
        startActivity(currentActivity);
    }
}
