package ru.einium.worktime;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Log.d("logtag", "MyApplication onCreate()");
        if (BuildConfig.FLAVOR.equals("broadcastStartService")) {
            context = getApplicationContext();
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
