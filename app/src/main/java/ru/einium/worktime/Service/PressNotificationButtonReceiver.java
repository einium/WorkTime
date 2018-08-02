package ru.einium.worktime.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.einium.worktime.model.Preference;
import ru.einium.worktime.model.WorkTimeModel;

public class PressNotificationButtonReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WorkTimeModel model = WorkTimeModel.getInstance();
        if (model.isStarted) {
            if (!model.isPaused){
                model.Pause();
            } else {
                model.Resume();
            }
        }
        model.saveCurrentState(new Preference());
    }
}