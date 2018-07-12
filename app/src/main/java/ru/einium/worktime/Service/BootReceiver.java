package ru.einium.worktime.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.einium.worktime.Service.TimeManagementService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("logtag", "BootReceiver onReceive()");
        //context.startService(new Intent(context, TimeManagementService.class));
    }
}