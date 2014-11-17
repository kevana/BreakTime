package com.breaktime;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;


public class StudyTimerActivity extends Activity implements SensorEventListener {
    private TextView timerTextView;
    private StudyTimerActivity studyTimerActivity;
    private StudyTimer studyTimer;
    private Long remainingMillis;

    private SensorManager sensorManager;
    private PowerManager powerManager;
    private Vibrator vibrator;
    private AudioManager audioManager;


    private Sensor proximitySensor;
    private PowerManager.WakeLock wl;
    private boolean timerRunning;
    int originalAudioLevel = AudioManager.RINGER_MODE_NORMAL;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_timer);

        studyTimerActivity = this;
        timerTextView = (TextView) findViewById(R.id.timerTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, getClass().getName());

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        originalAudioLevel = audioManager.getRingerMode();

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.v("StudyTimerActivity", "Preferences retrieved: "+settings.toString());

        // TODO: Set timer based on settings
        remainingMillis = settings.getLong(PrefID.STUDY_TIME_REMAINING, -1);
        if(remainingMillis < 0) {
            remainingMillis = settings.getLong(PrefID.STUDY_TIME, 12000L);
        }
        long seconds = remainingMillis / 1000;
        timerTextView.setText(String.format("%d", seconds));
//        Log.v("StudyTimerActivity", "millis:"+remainingMillis);
//        studyTimer = new StudyTimer(remainingMillis, 1000);
//        studyTimer.start();
//        Log.v("StudyTimerActivity", "StudyTimer Started");
//        timerRunning = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_timer, menu);
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
    public final void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        if (distance > 1.0 && timerRunning) {
            // Stop timer
            studyTimer.cleanup();
            Log.v("StudyTimerActivity", "StudyTimer Cancelled");
        } else if (distance < 1.0 && !timerRunning && hasWindowFocus()) {
            // Start timer
            studyTimer = new StudyTimer(remainingMillis, 1000);
            studyTimer.start();
            Log.v("StudyTimerActivity", "New StudyTimer Started");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        audioManager.setRingerMode(originalAudioLevel);
    }

    @Override
    protected void onDestroy() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onDestroy();
        sensorManager.unregisterListener(this);
        if(studyTimer != null) {
            studyTimer.cleanup();
        }
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void finishSession(View view) {
        Intent intent = new Intent(this, ChooseBreakActivity.class);
        startActivity(intent);
    }

    public class StudyTimer extends CountDownTimer {

        public StudyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            long seconds = millisInFuture / 1000;
            timerTextView.setText(String.format("%d", seconds));
            wl.acquire();
            vibrator.vibrate(new long[] {100L, 500L, 300L, 1000L}, -1);

            originalAudioLevel = audioManager.getRingerMode();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            timerRunning = true;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timerRunning = true;
            Log.v("StudyTimerActivity", "StudyTimer Ticked, Wakelock:"+wl.toString());
            remainingMillis = millisUntilFinished;
            // TODO: convert to minutes after testing
            long seconds = millisUntilFinished / 1000;
            timerTextView.setText(String.format("%d", seconds));
        }

        @Override
        public void onFinish() {
            this.cleanup();
            Intent intent = new Intent(studyTimerActivity, ChooseBreakActivity.class);
            startActivity(intent);
        }

        public void cleanup()
        {
            if(wl.isHeld()) {
                wl.release();
            }
            timerRunning = false;
            audioManager.setRingerMode(originalAudioLevel);
            if(remainingMillis > 0){
                SharedPreferences.Editor ed = settings.edit();
                ed.putLong(PrefID.STUDY_TIME_REMAINING, remainingMillis);
                ed.commit();
            }else
            {
                SharedPreferences.Editor ed = settings.edit();
                ed.putLong(PrefID.STUDY_TIME_REMAINING, -1);
                ed.commit();
            }
            this.cancel();
        }
    }
}
