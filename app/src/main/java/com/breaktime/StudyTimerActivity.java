package com.breaktime;

import android.app.Activity;
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
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;


public class StudyTimerActivity extends Activity implements SensorEventListener {
    int originalAudioLevel = AudioManager.RINGER_MODE_NORMAL;
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
    private SharedPreferences settings;

    private FaceUpTimer faceUpTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_timer);

        studyTimerActivity = this;
        timerTextView = (TextView) findViewById(R.id.timerTextView);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        remainingMillis = settings.getLong(PrefID.STUDY_TIME_REMAINING, -1);
        if (remainingMillis < 0) {
            remainingMillis = settings.getLong(PrefID.STUDY_TIME, 12000L);
        }

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = powerManager.newWakeLock(
                PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                getClass().getName());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        originalAudioLevel = audioManager.getRingerMode();

        Log.v("StudyTimerActivity", "millis:" + remainingMillis);
        long seconds = remainingMillis / 1000;
        timerTextView.setText(String.format("%d", seconds));

        //initialize and turn off timer
        studyTimer = new StudyTimer(remainingMillis, 1000);
        studyTimer.cleanup();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        faceUpTimer = new FaceUpTimer(3000, 1000);
        faceUpTimer.start();
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        if (distance > 1.0 && timerRunning) {
            // Stop timer
            studyTimer.cleanup();
            FlashController.flash(500);
            Log.v("StudyTimerActivity", "StudyTimer Cancelled");
            faceUpTimer = new FaceUpTimer(3000,1000);
            faceUpTimer.start();
        } else if (distance < 1.0 && !timerRunning && hasWindowFocus()) {
            // Start timer
            faceUpTimer.cancel();
            studyTimer = new StudyTimer(remainingMillis, 1000);
            vibrator.vibrate(new long[]{100L, 250L, 300L, 350L}, -1);
            FlashController.flash(200);
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
//        Handler h = new Handler();
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                audioManager.setRingerMode(originalAudioLevel);
//            }
//        }, 2000);
        if(settings.getLong(PrefID.STUDY_TIME_REMAINING, -1) < 0){
            remainingMillis = settings.getLong(PrefID.STUDY_TIME, 30000);
            long seconds = remainingMillis / 1000;
            timerTextView.setText(String.format("%d", seconds));
        }

    }

    @Override
    protected void onDestroy() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onDestroy();
        sensorManager.unregisterListener(this);
        if (studyTimer != null && timerRunning) {
            studyTimer.cleanup();
        }

        Log.v("studytimer", "study time remaining in prefs: " +
                settings.getLong(PrefID.STUDY_TIME_REMAINING, -5));
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void finishSession(View view) {

        studyTimer.onFinish();

    }

    private class FaceUpTimer extends CountDownTimer {

        public FaceUpTimer(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //meh
        }

        @Override
        public void onFinish() {
            //Make the directions image and text flash.
//            Log.d("FaceUpTimer", "Finished, animation should be going");
//            AnimationSet animation = new AnimationSet(false); //change to false
//            AnimationSet animation2 = new AnimationSet(false); //change to false
//
//                Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
////                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
//                fadeIn.setStartOffset(200);
//                fadeIn.setDuration(200);
//
//                Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
////                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
//                fadeOut.setStartOffset(0);
//                fadeOut.setDuration(200);
//
//            Animation fadeWait = new AlphaAnimation(1.0f, 1.0f);
////            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
//            fadeOut.setStartOffset(400);
//            fadeOut.setDuration(1000);
//
//                animation.addAnimation(fadeOut);
//                animation.addAnimation(fadeIn);
//
//                animation.setRepeatCount(Animation.INFINITE);
//
//                animation2.addAnimation(fadeOut);
//                animation2.addAnimation(fadeIn);
//
//                animation2.setRepeatCount(Animation.INFINITE);
//
//
//            //Get the image and textview
//            TextView textView = (TextView) findViewById(R.id.flipToStartText);
//            ImageView imageView = (ImageView) findViewById(R.id.flipDiagram);
//            textView.startAnimation(animation);
//            imageView.startAnimation(animation2);

        }
    }

    private class StudyTimer extends CountDownTimer {

        public StudyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            long seconds = millisInFuture / 1000;
            timerTextView.setText(String.format("%d", seconds));
            wl.acquire();

//            Handler h = new Handler();
//            h.postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                }
//            }, 2000);

            timerRunning = true;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timerRunning = true;
            Log.v("StudyTimerActivity", "StudyTimer Ticked, Wakelock: " + wl.toString());
            remainingMillis = millisUntilFinished;
            // TODO: convert to minutes after testing
            long seconds = millisUntilFinished / 1000;
            timerTextView.setText(String.format("%d", seconds));
        }

        @Override
        public void onFinish() {
            this.cleanup();
            studyTimerActivity.finish();

            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.STUDY_TIME_REMAINING, -1);
            ed.apply();

            Log.v("inStudytimer", "study time remaining in prefs: " +
                    settings.getLong(PrefID.STUDY_TIME_REMAINING, -5));

            Intent intent = new Intent(studyTimerActivity, ChooseBreakActivity.class);
            startActivity(intent);
        }

        public void cleanup() {
            if (wl.isHeld()) {
                wl.release();
            }
            timerRunning = false;
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {

                    audioManager.setRingerMode(originalAudioLevel);
                }
            }, 2000);

            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.STUDY_TIME_REMAINING, remainingMillis);
            ed.apply();

            this.cancel();
        }
    }
}
