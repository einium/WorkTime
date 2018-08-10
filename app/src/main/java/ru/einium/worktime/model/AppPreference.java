package ru.einium.worktime.model;

import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import ru.einium.worktime.MyApplication;

public class AppPreference {
    private static AppPreference instance;

    public static synchronized AppPreference getInstance() {
        if (instance == null) {
            instance = new AppPreference();
        }
        return instance;
    }
    private AppPreference(){}
    private SharedPreferences sp;

    private boolean defaultBool = true;
    private int defaultTime1 = 29700;
    private int defaultTime2 = 25200;
    private int defaultTime3 = 0;

    public MutableLiveData<Boolean> showNotification = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeAppOnReset = new MutableLiveData<>();
    public MutableLiveData<Integer> monday_s = new MutableLiveData<>();
    public MutableLiveData<Integer> tuesday_s = new MutableLiveData<>();
    public MutableLiveData<Integer> wednesday_s = new MutableLiveData<>();
    public MutableLiveData<Integer> thursday_s = new MutableLiveData<>();
    public MutableLiveData<Integer> friday_s = new MutableLiveData<>();
    public MutableLiveData<Integer> saturday_s = new MutableLiveData<>();
    public MutableLiveData<Integer> sunday_s = new MutableLiveData<>();

    private boolean isLoaded;

    public void loadSetting() {
        sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        Log.d("logtag", "AppPreference loadSetting()");
        showNotification.setValue(sp.getBoolean("isShowNotification", defaultBool));
        closeAppOnReset.setValue(sp.getBoolean("isCloseAppOnReset", defaultBool));
        monday_s.setValue(sp.getInt("Monday", defaultTime1));
        tuesday_s.setValue(sp.getInt("Tuesday", defaultTime1));
        wednesday_s.setValue(sp.getInt("Wednesday", defaultTime1));
        thursday_s.setValue(sp.getInt("Thursday", defaultTime1));
        friday_s.setValue(sp.getInt("Friday", defaultTime2));
        saturday_s.setValue(sp.getInt("Saturday", defaultTime3));
        sunday_s.setValue(sp.getInt("Sunday", defaultTime3));
        isLoaded = true;
    }

    public void setShowNotification(boolean showNotification) {
        this.showNotification.postValue(showNotification);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isShowNotification", showNotification);
        editor.apply();
    }

    public void setCloseAppOnReset(boolean closeAppOnReset) {
        this.closeAppOnReset.postValue(closeAppOnReset);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isCloseAppOnReset", closeAppOnReset);
        editor.apply();
    }

    public void setMonday(int monday) {
        this.monday_s.postValue(monday);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Monday", monday);
        editor.apply();
    }

    public void setTuesday(int tuesday) {
        this.tuesday_s.postValue(tuesday);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Tuesday", tuesday);
        editor.apply();
    }

    public void setWednesday(int wednesday) {
        this.wednesday_s.postValue(wednesday);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Wednesday", wednesday);
        editor.apply();
    }

    public void setThursday(int thursday) {
        this.thursday_s.postValue(thursday);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Thursday", thursday);
        editor.apply();
    }

    public void setFriday(int friday) {
        this.friday_s.postValue(friday);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Friday", friday);
        editor.apply();
    }

    public void setSaturday(int saturday) {
        this.saturday_s.postValue(saturday);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Saturday", saturday);
        editor.apply();
    }

    public void setSunday(int sunday) {
        this.sunday_s.postValue(sunday);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Sunday", sunday);
        editor.apply();
    }

    public boolean isShowNotification() {
        if (showNotification.getValue() != null)
            return showNotification.getValue();
        return defaultBool;
    }

    public boolean isCloseAppOnReset() {
        if (closeAppOnReset.getValue() != null)
            return closeAppOnReset.getValue();
        return defaultBool;
    }

    public int getMondayInSec() {
        if (monday_s.getValue() != null)
            return monday_s.getValue();
        return defaultTime1;
    }

    public int getTuesdayInSec() {
        if (tuesday_s.getValue() != null)
            return tuesday_s.getValue();
        return defaultTime1;
    }

    public int getWednesdayInSec() {
        if (wednesday_s.getValue() != null)
            return wednesday_s.getValue();
        return defaultTime1;
    }

    public int getThursdayInSec() {
        if (thursday_s.getValue() != null)
            return thursday_s.getValue();
        return defaultTime1;
    }

    public int getFridayInSec() {
        if (friday_s.getValue() != null)
            return friday_s.getValue();
        return defaultTime2;
    }

    public int getSaturdayInSec() {
        if (saturday_s.getValue() != null)
            return saturday_s.getValue();
        return defaultTime3;
    }

    public int getSundayInSec() {
        if (sunday_s.getValue() != null)
            return sunday_s.getValue();
        return defaultTime3;
    }

    public boolean needLoad() {
        return !isLoaded;
    }
}
