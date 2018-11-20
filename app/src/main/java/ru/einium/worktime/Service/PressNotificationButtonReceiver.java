package ru.einium.worktime.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.einium.worktime.model.TimePreference;
import ru.einium.worktime.model.WorkTimeModel;

public class PressNotificationButtonReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("logtag", "PressNotificationButtonReceiver onReceive");
        WorkTimeModel model = WorkTimeModel.getInstance();
        if (model.isStarted) {
            if (!model.isPaused){
                model.Pause();
            } else {
                model.Resume();
            }
        }
        model.saveCurrentState(new TimePreference());
    }
}