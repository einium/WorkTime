package com.compassplus.worktime;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class Preference {
    private SharedPreferences sp;

    public Preference(Context context) {
        sp = context.getSharedPreferences("timeState", Context.MODE_PRIVATE);
    }

    public void saveCurrentState(long commonWorkTime,
                                 long currentStartTime,
                                 long commonTimeOut,
                                 long currentTimeOutStartTime,
                                 long customWorkTime,
                                 boolean isPaused) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("currentDate", System.currentTimeMillis());
        editor.putLong("commonWorkTime", commonWorkTime);
        editor.putLong("currentStartTime", currentStartTime);
        editor.putBoolean("isPaused", isPaused);
        editor.putLong("commonTimeOut", commonTimeOut);
        editor.putLong("currentTimeOutStartTime", currentTimeOutStartTime);
        editor.putLong("customWorkTime", customWorkTime);
        editor.apply();

        Log.d("logtag", "current state saved in preference");
    }

    public boolean loadSavedState(){
        Log.d("logtag", "Preference try load state");
        long savedDate = sp.getLong("currentDate", 0L);
        if (savedDate == 0L) return false;
        Calendar savedDateCalendar = Calendar.getInstance();
        savedDateCalendar.setTimeInMillis(savedDate);
        Calendar currentDateCalendar = Calendar.getInstance();
        return currentDateCalendar.get(Calendar.DAY_OF_YEAR) == savedDateCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public long loadCommonWorkTime(){
        return sp.getLong("commonWorkTime", 0L);
    }
    public long loadCurrentStartTime(){
        return sp.getLong("currentStartTime", 0L);
    }
    public boolean loadPaused(){
        return sp.getBoolean("isPaused", false);
    }
    public long loadCommonTimeOut(){
        return sp.getLong("commonTimeOut", 0L);
    }
    public long loadCurrentTimeOutStartTime(){
        return sp.getLong("currentTimeOutStartTime", 0L);
    }

    public long loadCustomWorkTime() {
        return sp.getLong("customWorkTime", 0L);
    }
}
