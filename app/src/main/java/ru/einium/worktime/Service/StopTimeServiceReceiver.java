package ru.einium.worktime.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopTimeServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("logtag", "StopTimeServiceReceiver onReceive()");
        context.stopService(new Intent(context, TimeManagementService.class));
    }
}
