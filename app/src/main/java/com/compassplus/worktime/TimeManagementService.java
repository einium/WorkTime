package com.compassplus.worktime;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TimeManagementService extends Service {
    Handler handler;
    Runnable runnable;

    public TimeManagementService() {
        Log.d("logtag", "TimeManagementService()");
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("logtag", "run()");
                handler.postDelayed(runnable, 2000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("logtag", "TimeManagementService onBind()");
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("logtag", "TimeManagementService onDestroy()");
    }
}
