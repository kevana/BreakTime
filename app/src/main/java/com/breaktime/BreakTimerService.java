package com.breaktime;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class BreakTimerService extends Service {

    private BreakTimerService breakTimerService;
    private WindowManager windowManager;
    private TextView chatHeadText;
    private SharedPreferences settings;
    private BreakTimer breakTimer;

    public BreakTimerService() {
        breakTimerService = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Unused
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        breakTimerService = this;
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        chatHeadText = new TextView(this);
        // TODO: Change to TextView with timer?
        chatHeadText.setTextSize(25);
        chatHeadText.setBackgroundResource(R.drawable.bubble_background);
        chatHeadText.setTextColor(getResources().getColor(R.color.whiteFull));
        chatHeadText.setGravity(Gravity.CENTER);

        remainingMillis = settings.getLong(PrefID.BREAK_TIME, -1);
        if (remainingMillis < 0) {

        }

        breakTimer = new BreakTimer(remainingMillis, 1000);
        breakTimer.start();
        long seconds = remainingMillis / 1000;
        chatHeadText.setText(String.format("%d", seconds));
        Log.v("BreakTimerService", "New BreakTimer Started");

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;

        params.x = 50;
        params.y = 100;

        chatHeadText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), BackToWorkActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);
                    }
                }
        );

        windowManager.addView(chatHeadText, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHeadText != null) windowManager.removeView(chatHeadText);
    }

    long remainingMillis;

    public class BreakTimer extends CountDownTimer {

        public BreakTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            long seconds = millisInFuture / 1000;
            chatHeadText.setText(String.format("%d", seconds));
            //vibrator.vibrate(new long[] {100L, 500L, 300L, 1000L}, -1);

            //Delay running this until after vibration is done
            //originalAudioLevel = audioManager.getRingerMode();
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }, 2000);

            //timerRunning = true;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //timerRunning = true;
            Log.v("BreakTimerService", "BreakTimer Ticked");
            remainingMillis = millisUntilFinished;
            // TODO: convert to minutes after testing
            long seconds = millisUntilFinished / 1000;
            chatHeadText.setText(String.format("%d", seconds));
        }

        @Override
        public void onFinish() {
            this.cleanup();
            Log.v("BreakTimerService", "BreakTimer Finished");
            Intent intent = new Intent(getBaseContext(), BackToWorkActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
        }

        public void cleanup() {
            //timerRunning = false;
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //        audioManager.setRingerMode(originalAudioLevel);
                }
            }, 2000);
/*
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.STUDY_TIME_REMAINING, remainingMillis);
            ed.commit();
*/
            this.cancel();
        }
    }
}
