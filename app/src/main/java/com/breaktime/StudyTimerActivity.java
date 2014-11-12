package com.breaktime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class StudyTimerActivity extends Activity implements SensorEventListener {
    private TextView timerTextView;
    private StudyTimerActivity studyTimerActivity;
    private StudyTimer studyTimer;
    private Long remainingMillis;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private boolean timerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_timer);

        studyTimerActivity = this;
        timerTextView = (TextView) findViewById(R.id.timerTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // TODO: Set timer based on settings
        studyTimer = new StudyTimer(30000, 1000);
        studyTimer.start();
        timerRunning = true;
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
            studyTimer.cancel();
        } else if (distance < 1.0 && !timerRunning) {
            // Start timer
            studyTimer = new StudyTimer(remainingMillis, 1000);
            studyTimer.start();
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
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
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
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timerRunning = true;
            remainingMillis = millisUntilFinished;
            // TODO: convert to minutes after testing
            long seconds = millisUntilFinished / 1000;
            timerTextView.setText(String.format("%d", seconds));
        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(studyTimerActivity, ChooseBreakActivity.class);
            startActivity(intent);
        }
    }
}
