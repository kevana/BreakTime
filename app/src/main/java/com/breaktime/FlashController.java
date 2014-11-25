package com.breaktime;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;



/**
 * Created by Patrick on 11/24/2014.
 */
public class FlashController {
    static Camera cam = null;

    public static void flash(int millis){
        cam = Camera.open();
        Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
        cam.startPreview();
        Log.v("FlashController", "Flash fired.");
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                cam.stopPreview();
                cam.release();
                Log.v("FlashController", "Flash stopped.");
            }
        }, millis);
    }
}
