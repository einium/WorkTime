package com.compassplus.worktime;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TimeManagementService extends Service {

    public TimeManagementService() {
        Log.d("logtag", "TimeManagementService()");

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
