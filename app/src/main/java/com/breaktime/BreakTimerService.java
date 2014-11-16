package com.breaktime;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

public class BreakTimerService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;

    public BreakTimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Unused
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        chatHead = new ImageView(this);
        // TODO: Change to TextView with timer?
        chatHead.setImageResource(R.drawable.settings48);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;

        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
