package ru.einium.worktime.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import ru.einium.worktime.R;
import ru.einium.worktime.AppPreference;
import ru.einium.worktime.model.TimePreference;
import ru.einium.worktime.model.WorkTimeModel;
import ru.einium.worktime.view.MainActivity;
import ru.einium.worktime.viewmodel.IChangeTimeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeManagementService extends Service {
    private BroadcastReceiver receiver;
    private AppPreference setting = AppPreference.getInstance();
    private CharSequence channelName = "time";
    String CHANNEL_ID = "work_time_channel";
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
            if ((time / 1000)%60 == 0) {
                workTime = convertTimeToString(time);
                if (setting.isShowNotification()) {
                    showNotification();
                }
            }
        }

        @Override
        public void OnTimeOutChange(Long time) {
            if ((time / 1000) % 60 == 0) {
                timeOut = convertTimeToString(time);
                if (setting.isShowNotification()) {
                    showNotification();
                }
            }
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

        @Override
        public void OnPeriodicSignalCalled() {
            Log.d("logtag", "OnPeriodicSignalCalled()");
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibe != null) {
                long[] pattern = { 0, 150, 100, 150, 100, 150};
                vibe.vibrate(pattern, -1);
            }
        }

        @Override
        public void OnPreEndSignalCalled() {
            Log.d("logtag", "OnPreEndSignalCalled()");
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibe != null) {
                long[] pattern = { 0, 1000};
                vibe.vibrate(pattern, -1);
            }
        }
    };
    private Observer<Boolean> changeShowNotificationObserver = b -> {
        if (b != null && !b) {
            dismissNotification();
        }
    };

    public TimeManagementService() {
        Log.d("logtag", "TimeManagementService()");
        model = WorkTimeModel.getInstance();
        if (model.getGlobalStartTime() == 0) {
            model.loadSavedState(new TimePreference());
        }
        if (setting.needLoad()) {
            setting.loadSetting();
        }
        model.addListener(listener);
        setting.showNotification.observeForever(changeShowNotificationObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("logtag", "TimeManagementService onStartCommand()");
        //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Notification startNotification = createNotification(notificationManager);
        //startForeground(notificationID, startNotification);

        receiver = new PressNotificationButtonReceiver();
        registerReceiver(receiver, new IntentFilter("Press_time_action_button"));
        return START_STICKY;
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null && isScreenOn()) {
            Notification notification = createNotification(notificationManager);
            notificationManager.notify(notificationID, notification);
        }
    }
    private boolean isScreenOn() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                return pm.isInteractive();
            } else {
                return pm.isScreenOn();
            }
        }
        return true;
    }

    private Notification createNotification(NotificationManager notificationManager){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel description");
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 4445, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WorkTime")
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.notif_icon);

        RemoteViews notificationLayout = createNotificationLayout();
        notificationBuilder.setContentIntent(pendingIntent)
                .setContent(notificationLayout)
                .setWhen(System.currentTimeMillis());
        return notificationBuilder.build();
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
        Intent actionButtonIntent = new Intent(this, PressNotificationButtonReceiver.class);
        actionButtonIntent.setAction("Press_time_action_button");
        actionButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent actionButtonPendingIntent = PendingIntent.getBroadcast(this, 0, actionButtonIntent, 0);
        notificationLayout.setOnClickPendingIntent(R.id.btn_notif_action, actionButtonPendingIntent);
        return notificationLayout;
    }

    private void dismissNotification() {
        try {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(notificationID);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String convertTimeToString(long time) {
        if (time == 0) return "--:--";
        DateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(time));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("logtag", "TimeManagementService onDestroy()");
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (listener != null) {
            model.removeListener(listener);
        }
        setting.showNotification.removeObserver(changeShowNotificationObserver);
        dismissNotification();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("logtag", "TimeManagementService onTaskRemoved");

        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmService != null) {
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
        }
    }
}

