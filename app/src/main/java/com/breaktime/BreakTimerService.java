package com.breaktime;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class BreakTimerService extends Service {

    long remainingMillis;
    private WindowManager windowManager;
    private TextView chatHeadText;
    private WindowManager.LayoutParams params;
    public static boolean serviceRunning;
    BreakTimer breakTimer;
    BreakTimerService breakTimerService;

    @Override
    public IBinder onBind(Intent intent) {
        // Unused
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        breakTimerService = this;

        chatHeadText = new TextView(this);
        // TODO: Change to TextView with timer?
        chatHeadText.setTextSize(25);
        chatHeadText.setBackgroundResource(R.drawable.bubble_background);
        chatHeadText.setTextColor(getResources().getColor(R.color.whiteFull));
        chatHeadText.setGravity(Gravity.CENTER);

        remainingMillis = settings.getLong(PrefID.BREAK_TIME, -1);

        breakTimer = new BreakTimer(remainingMillis, 1000);
        breakTimer.start();
        long seconds = remainingMillis / 1000;
        chatHeadText.setText(String.format("%d", seconds));
        Log.v("BreakTimerService", "New BreakTimer Started");
        serviceRunning = true;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
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

        chatHeadText.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean moveOccurred = false;
            int displacementX;
            int displacementY;

            // From http://www.piwai.info/chatheads-basics
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        moveOccurred = false;
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.v("BreakTimerMove:", ""+ (Math.abs(displacementX) + Math.abs(displacementY)));
                        if(Math.abs(displacementX) + Math.abs(displacementY) < 20){
                            //Action was a click
                            breakTimerService.stopSelf();
                            Intent intent = new Intent(getBaseContext(), BackToWorkActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(intent);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        displacementX = (int) (event.getRawX() - initialTouchX);
                        displacementY = (int) (event.getRawY() - initialTouchY);
                        params.x = initialX + displacementX;
                        params.y = initialY + displacementY;
                        windowManager.updateViewLayout(chatHeadText, params);
                        return true;
                }
                return false;
            }
        });


        windowManager.addView(chatHeadText, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHeadText != null) windowManager.removeView(chatHeadText);
        breakTimer.cancel();
        serviceRunning = false;
    }

    public class BreakTimer extends CountDownTimer {

        final int TICK_MOD = 3;


        public BreakTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture * 3, countDownInterval);
            long seconds = millisInFuture / (1000 * TICK_MOD);
            chatHeadText.setText(String.format("%d", seconds));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.v("BreakTimerService", "BreakTimer Ticked");
            remainingMillis = millisUntilFinished;
            // TODO: convert to minutes after testing
            long seconds = millisUntilFinished / (1000 * TICK_MOD);
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
            this.cancel();
        }
    }
}
