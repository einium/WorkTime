package com.compassplus.worktime.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.compassplus.worktime.Service.IManageServiceListener;
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
    private IManageServiceListener serviceListener;

    public MutableLiveData<Boolean> isPaused = new MutableLiveData<>();
    public MutableLiveData<Boolean> isStarted = new MutableLiveData<>();
    public MutableLiveData<String> startTimeText = new MutableLiveData<>();
    public MutableLiveData<String> workingTimeText = new MutableLiveData<>();
    public MutableLiveData<String> timeOutText = new MutableLiveData<>();
    public MutableLiveData<String> stopTimeText = new MutableLiveData<>();
    public MutableLiveData<String> overTimeText = new MutableLiveData<>();
    public MutableLiveData<String> workDayText = new MutableLiveData<>();

    public WorkTimeViewModel() {
        Log.d("logtag", "WorkTimeViewModel()");
        model = WorkTimeModel.getInstance();
          startTimeText.postValue(defValue);
        workingTimeText.postValue(defValue);
            timeOutText.postValue(defValue);
           stopTimeText.postValue(defValue);
           overTimeText.postValue(defValue);
            workDayText.postValue(convertTimeToStringCorrectly(getWorkDayHours(), getWorkDayMinutes()));
           model.addListener(new IChangeTimeListener(){
               @Override
               public void OnStartTimeChange(Long time) {
                   startTimeText.postValue(convertTimeToString(time, false));
               }

               @Override
               public void OnWorkingTimeChange(Long time) {
                   workingTimeText.postValue(convertTimeToString(time, true));
               }

               @Override
               public void OnTimeOutChange(Long time) {
                   timeOutText.postValue(convertTimeToString(time, true));
               }

               @Override
               public void OnStopTimeChange(Long time) {
                   stopTimeText.postValue(convertTimeToString(time, false));
               }

               @Override
               public void OnOverTimeChange(Long time) {
                   overTimeText.postValue(convertTimeToString(time, true));
               }
           });
           isStarted.postValue(model.isStarted);
           isPaused.postValue(model.isPaused);
    }

    public void OnClickButton() {
        Log.d("logtag", "WorkTimeViewModel OnClickButton()");
        if (!model.isStarted) {
            model.Start();
            //startTimeText.setValue(convertTimeToString(model.getStartTime(), false));
            //stopTimeText.setValue(convertTimeToString(model.getStopTime(), false));
            isPaused.setValue(false);
            serviceListener.startService();
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

    private String convertTimeToString(long time, boolean setTimeZone) {
        if (time == 0) return defValue;

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        if (setTimeZone) formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        Log.d("logtag", "WorkTimeViewModel resetTimer()");
        if (model != null) {
            model.reset();
            startTimeText.setValue(convertTimeToString(0, false));
            isStarted.setValue(false);
            serviceListener.stopService();
        }
    }

    public void setServiceListener(IManageServiceListener listener){
        if (listener != null)
            serviceListener = listener;
    }
}
