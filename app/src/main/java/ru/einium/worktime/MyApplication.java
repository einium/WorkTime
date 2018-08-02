package ru.einium.worktime;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Log.d("logtag", "MyApplication onCreate()");
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        Log.d("logtag", "MyApplication getAppContext()");
        return context;
    }

}
