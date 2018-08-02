package ru.einium.worktime.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.einium.worktime.MyApplication;

public class Preference {
    private SharedPreferences sp;

    public Preference() {
        sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
    }

    public void saveCurrentState(boolean isStarted,
                                 long globalStartTime,
                                 long currentStartTime,
                                 long commonWorkTime,
                                 long currentWorkTime,
                                 boolean isPaused,
                                 long currentTimeOutStartTime,
                                 long commonTimeOut,
                                 long currentTimeOut,
                                 long customWorkTime) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isStarted", isStarted);
        editor.putLong("globalStartTime", globalStartTime);
        editor.putLong("currentStartTime", currentStartTime);
        editor.putLong("commonWorkTime", commonWorkTime);
        editor.putLong("currentWorkTime", currentWorkTime);
        editor.putBoolean("isPaused", isPaused);
        editor.putLong("currentTimeOutStartTime", currentTimeOutStartTime);
        editor.putLong("commonTimeOut", commonTimeOut);
        editor.putLong("currentTimeOut", currentTimeOut);
        editor.putLong("customWorkTime", customWorkTime);
        editor.apply();
    }
    public boolean loadStarted(){
        return sp.getBoolean("isStarted", false);
    }
    public long loadGlobalStartTime(){
        return sp.getLong("globalStartTime", 0L);
    }
    public long loadCurrentStartTime(){
        return sp.getLong("currentStartTime", 0L);
    }
    public long loadCommonWorkTime(){
        return sp.getLong("commonWorkTime", 0L);
    }
    public long loadCurrentWorkTime(){
        return sp.getLong("currentWorkTime", 0L);
    }
    public boolean loadPaused(){
        return sp.getBoolean("isPaused", false);
    }
    public long loadCurrentTimeOutStartTime(){
        return sp.getLong("currentTimeOutStartTime", 0L);
    }
    public long loadCommonTimeOut(){
        return sp.getLong("commonTimeOut", 0L);
    }
    public long loadCurrentTimeOut(){
        return sp.getLong("currentTimeOut", 0L);
    }
    public long loadCustomWorkTime(){
        return sp.getLong("customWorkTime", 0L);
    }
}
