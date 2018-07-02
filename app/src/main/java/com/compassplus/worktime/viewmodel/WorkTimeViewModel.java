package com.compassplus.worktime.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.compassplus.worktime.TimeManagementService;
import com.compassplus.worktime.model.WorkTimeModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WorkTimeViewModel extends ViewModel {
    private int msInHour = 3600000;
    private int msInMinute = 60000;
    private String defValue = "--:--";
    private WorkTimeModel model;
    private IChangeTimeListener listener;

    public MutableLiveData<Boolean> isPaused = new MutableLiveData<>();
    public MutableLiveData<Boolean> isStarted = new MutableLiveData<>();
    public MutableLiveData<String> startTimeText = new MutableLiveData<>();
    public MutableLiveData<String> workingTimeText = new MutableLiveData<>();
    public MutableLiveData<String> timeOutText = new MutableLiveData<>();
    public MutableLiveData<String> stopTimeText = new MutableLiveData<>();
    public MutableLiveData<String> overTimeText = new MutableLiveData<>();
    public MutableLiveData<String> workDayText = new MutableLiveData<>();

    public WorkTimeViewModel() {
        model = WorkTimeModel.getInstance();
          startTimeText.setValue(defValue);
        workingTimeText.setValue(defValue);
            timeOutText.setValue(defValue);
           stopTimeText.setValue(defValue);
           overTimeText.setValue(defValue);
            workDayText.setValue(convertTimeToStringCorrectly(getWorkDayHours(), getWorkDayMinutes()));
           listener = new IChangeTimeListener(){
               @Override
               public void OnWorkingTimeChange(Long time) {
                   workingTimeText.setValue(convertTimeToString(time, true));
               }

               @Override
               public void OnTimeOutChange(Long time) {
                   timeOutText.setValue(convertTimeToString(time, true));
               }

               @Override
               public void OnStopTimeChange(Long time) {
                   stopTimeText.setValue(convertTimeToString(time, false));
               }

               @Override
               public void OnOverTimeChange(Long time) {
                   overTimeText.setValue(convertTimeToString(time, true));
               }
           };
           model.addListener(listener);
    }

    public void OnClickButton(Context context) {
        if (!model.isStarted) {
            model.Start();
            startTimeText.setValue(convertTimeToString(model.getStartTime(), false));
            stopTimeText.setValue(convertTimeToString(model.getStopTime(), false));
            isPaused.setValue(false);

            Intent intentService = new Intent(context, TimeManagementService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService);
            }else{
                context.startService(intentService);
            }
        }else{
            if (!model.isPaused){
                model.Pause();
                isPaused.setValue(true);
            }else{
                model.Resume();
                isPaused.setValue(false);
            }
        }
    }

    private String convertTimeToString(long time, boolean allowTimeZone) {
        if (time == 0) return defValue;

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        if (allowTimeZone) formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(time));
    }

    public int getWorkDayHours() {
        long workDay = 0;
        if (model != null)
            workDay = model.getWorkDayInMillis();
        return (int) workDay / msInHour;
    }

    public int getWorkDayMinutes() {
        long workDay = 0;
        if (model != null)
            workDay = model.getWorkDayInMillis();
        int hours = (int) workDay / msInHour;
        return (int) (workDay - hours*msInHour)/msInMinute;
    }

    public void setNewWorkTime(int hourOfDay, int minute) {
        long workTimeInMillis = hourOfDay*msInHour + minute*msInMinute;
        if (model != null)
            model.setWorkTimeInMillis(workTimeInMillis);
        workDayText.setValue(convertTimeToStringCorrectly(hourOfDay, minute));
    }

    private String convertTimeToStringCorrectly(int hourOfDay, int minute){
        if (minute < 10) {
            return String.valueOf(hourOfDay) + ":0" + String.valueOf(minute);
        }
        return String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
    }

    public void resetTimer() {
        if (model != null) {
            model.reset();
            startTimeText.setValue(convertTimeToString(0, false));
            isStarted.setValue(false);
        }
    }

    //public void OnDestroyApp(Preference pref) {
    //    model.OnDestroyApp(pref);
    //}

    public interface IChangeTimeListener{
        void OnWorkingTimeChange(Long time);
        void OnTimeOutChange(Long time);
        void OnStopTimeChange(Long time);
        void OnOverTimeChange(Long time);
    }
}
