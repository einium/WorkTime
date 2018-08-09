package ru.einium.worktime.Service;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import ru.einium.worktime.BuildConfig;
import ru.einium.worktime.MyApplication;
import ru.einium.worktime.R;
import ru.einium.worktime.model.Preference;
import ru.einium.worktime.model.WorkTimeModel;
import ru.einium.worktime.view.MainActivity;
import ru.einium.worktime.viewmodel.IChangeTimeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeManagementService extends Service {
    private CharSequence channelName = "time";
    public static int notificationID = 13056;
    private String workTime = "";
    private String timeOut = "";
    private WorkTimeModel model;

    private IChangeTimeListener listener = new IChangeTimeListener(){
        @Override
        public void OnStartTimeChange(Long time) {
        }

        @Override
        public void OnWorkingTimeChange(Long time) {
            workTime = convertTimeToString(time);
            showNotification();
        }

        @Override
        public void OnTimeOutChange(Long time) {
            timeOut = convertTimeToString(time);
            showNotification();
        }

        @Override
        public void OnStopTimeChange(Long time) {
        }

        @Override
        public void OnOverTimeChange(Long time) {
        }

        @Override
        public void OnStartedChanged(boolean started) {
        }

        @Override
        public void OnPausedChanged(boolean paused) {
        }
    };

    public TimeManagementService() {
        Log.d("logtag", "TimeManagementService()");
        model = WorkTimeModel.getInstance();
        if (model.getGlobalStartTime() == 0) {
            model.loadSavedState(new Preference(this));
        }
        model.addListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            String CHANNEL_ID = "work_time_channel";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 4445, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("WorkTime")
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.notidication_icon);

            RemoteViews notificationLayout = createNotificationLayout();
            notificationBuilder.setContentIntent(pendingIntent)
                    .setContent(notificationLayout)
                    .setWhen(System.currentTimeMillis());
            notificationManager.notify(notificationID, notificationBuilder.build());
        }
    }

    private RemoteViews createNotificationLayout(){
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);
        notificationLayout.setTextViewText(R.id.tv_notif_workTimeValue, workTime);
        notificationLayout.setTextViewText(R.id.tv_notif_timeOutValue, timeOut);
        String btnText;
        if (model != null && !model.isPaused) {
            btnText = getResources().getString(R.string.pause);
        } else {
            btnText = getResources().getString(R.string.resume);
        }
        notificationLayout.setTextViewText(R.id.btn_notif_action, btnText);
        Intent actionButtonIntent = new Intent("Press_time_action_button");
        actionButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent actionButtonPendingIntent = PendingIntent.getBroadcast(this, 0, actionButtonIntent, 0);
        notificationLayout.setOnClickPendingIntent(R.id.btn_notif_action, actionButtonPendingIntent);
        return notificationLayout;
    }

    private void dismisNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationID);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String convertTimeToString(long time) {
        if (time == 0) return "--:--:--";
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(time));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("logtag", "TimeManagementService onDestroy()");
        model.removeListener(listener);
        if (model.isStarted) {
            if (BuildConfig.FLAVOR.equals("directStartService")){
                startService(new Intent(MyApplication.getAppContext(), TimeManagementService.class));
            } else {
                Intent intent = new Intent("Start_worktime_service");
                sendBroadcast(intent);
            }
        } else {
            dismisNotification();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("logtag", "TimeManagementService onTaskRemoved");
        model.removeListener(listener);
        if (model.isStarted) {
            if (BuildConfig.FLAVOR.equals("directStartService")){
                startService(new Intent(this, TimeManagementService.class));
            } else {
                Intent intent = new Intent("Start_worktime_service");
                sendBroadcast(intent);
            }
        } else {
            dismisNotification();
        }
    }
}

