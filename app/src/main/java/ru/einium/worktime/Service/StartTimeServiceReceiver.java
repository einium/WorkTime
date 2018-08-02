package ru.einium.worktime.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartTimeServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("logtag", "StartTimeServiceReceiver onReceive()");
        context.startService(new Intent(context, TimeManagementService.class));
    }
}
