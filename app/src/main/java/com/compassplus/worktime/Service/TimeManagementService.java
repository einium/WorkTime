package com.compassplus.worktime.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.compassplus.worktime.R;
import com.compassplus.worktime.model.WorkTimeModel;
import com.compassplus.worktime.view.MainActivity;
import com.compassplus.worktime.viewmodel.IChangeTimeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeManagementService extends Service {

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public TimeManagementService getService() {
            return TimeManagementService.this;
        }
    }

    private CharSequence channelName = "time";
    public static int notificationID = 13056;
    private String workTime = "";
    private String timeOut = "";
    private Notification notification;
    private NotificationManager notificationManager;
    private WorkTimeModel model;
    private IChangeTimeListener listener = new IChangeTimeListener(){
        @Override
        public void OnStartTimeChange(Long time) {
        }

        @Override
        public void OnWorkingTimeChange(Long time) {
            Log.d("logtag", "OnWorkingTimeChange time: " + time.toString());
            workTime = convertTimeToString(time);

            createNotification();
            if (notificationManager != null && notification != null) {
                notificationManager.notify(notificationID, notification);
            }
        }

        @Override
        public void OnTimeOutChange(Long time) {
        }

        @Override
        public void OnStopTimeChange(Long time) {
        }

        @Override
        public void OnOverTimeChange(Long time) {
        }
    };

    public TimeManagementService() {
        Log.d("logtag", "TimeManagementService()");
        model = WorkTimeModel.getInstance();
        model.addListener(listener);
    }

    @Override
    public void onCreate() {
        Log.d("logtag", "TimeManagementService onCreate()");
    }

    public void createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 4445, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //create notification layout
        /*RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);
        notificationLayout.setTextViewText(R.id.tv_notif_workTimeValue, workTime);
        notificationLayout.setTextViewText(R.id.tv_notif_timeOutValue, timeOut);
        String btnText;
        if (model != null && !model.isPaused) {
            btnText = getResources().getString(R.string.pause);
        } else {
            btnText = getResources().getString(R.string.resume);
        }
        notificationLayout.setTextViewText(R.id.tv_notif_btn, btnText);*/

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String notificationTitle = "WorkTime";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String CHANNEL_ID = "work_time_channel";
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(workTime)
                    //.setCustomContentView(notificationLayout)
                    .setSmallIcon(R.drawable.notidication_icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .build();
            if (notificationManager != null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
                notificationManager.createNotificationChannel(channel);
            }
        } else {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentIntent(pendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(workTime)
                    //.setContent(notificationLayout)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.notidication_icon)
                    //.setOngoing(true)
                    .setAutoCancel(true);
            notification = builder.build();
        }
    }

    public void dismisNotification() {
        Log.d("logtag", "dismisNotification()");
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationID);
        }
    }

    private String convertTimeToString(long time) {
        if (time == 0) return "--:--";
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(time));
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("logtag", "TimeManagementService onBind()");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("logtag", "TimeManagementService onDestroy()");
        Intent serviceIntent = new Intent(getBaseContext(), TimeManagementService.class);
        if (model.isStarted) {
            startService(serviceIntent);
        }
        model.removeListener(listener);
    }
}
