package ru.einium.worktime.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import ru.einium.worktime.Service.IManageServiceListener;
import ru.einium.worktime.Service.TimeManagementService;
import ru.einium.worktime.model.Preference;
import ru.einium.worktime.model.WorkTimeModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

               @Override
               public void OnStartedChanged(boolean started) {
                   isStarted.postValue(started);
               }

               @Override
               public void OnPausedChanged(boolean paused) {
                    isPaused.postValue(paused);
               }
           });
    }

    public void loadSavedState(Context context){
        if (model != null) {
            if (model.getGlobalStartTime() == 0){
                Log.d("logtag", "model.loadSavedState()");
                model.loadSavedState(new Preference(context));
                workDayText.postValue(convertTimeToStringCorrectly(getWorkDayHours(), getWorkDayMinutes()));
                if (serviceListener != null) {
                    serviceListener.startService();
                }
            }
        }
    }

    public void OnClickButton(Context context) {
        Log.d("logtag", "WorkTimeViewModel OnClickButton()");
        if (!model.isStarted) {
            model.Start();
            if (serviceListener != null) {
                serviceListener.startService();
            }
        }else{
            if (!model.isPaused){
                model.Pause();
            }else{
                model.Resume();
            }
        }
        model.saveCurrentState(new Preference(context));
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

    public void setNewWorkTime(int hourOfDay, int minute, Context context) {
        long workTimeInMillis = hourOfDay*msInHour + minute*msInMinute;
        if (model != null){
            model.setWorkTimeInMillis(workTimeInMillis);
            model.saveCurrentState(new Preference(context));
        }
        workDayText.setValue(convertTimeToStringCorrectly(hourOfDay, minute));
    }

    public int getStartTimeHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.getStartTime());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getStartTimeMinute() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.getStartTime());
        return calendar.get(Calendar.MINUTE);
    }

    public void setStartTime(Context context, int hourOfDay, int minute) {
        Calendar newStartTime = Calendar.getInstance();
        newStartTime.setTimeInMillis(System.currentTimeMillis());
        newStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newStartTime.set(Calendar.MINUTE, minute);
        newStartTime.set(Calendar.SECOND, 0);
        newStartTime.set(Calendar.MILLISECOND, 0);
        newStartTime.setTimeZone(TimeZone.getDefault());

        if (model != null) {
            long maxAllowedTime = model.getMaxAllowedStartTime();
            //проверка, если пользователь выбрал еще не наступившее время
            if (newStartTime.getTimeInMillis() < maxAllowedTime) {
                model.setStartTime(newStartTime.getTimeInMillis());
            } else {
                model.setStartTime(maxAllowedTime);
            }
            model.saveCurrentState(new Preference(context));
        }
    }

    private String convertTimeToStringCorrectly(int hourOfDay, int minute){
        if (minute < 10) {
            return String.valueOf(hourOfDay) + ":0" + String.valueOf(minute);
        }
        return String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
    }

    public void resetTimer(Context context) {
        Log.d("logtag", "WorkTimeViewModel resetTimer()");
        if (model != null) {
            model.reset();
            model.saveCurrentState(new Preference(context));

            startTimeText.setValue(convertTimeToString(0, false));
            if (serviceListener != null){
                serviceListener.stopService();
            }
        }
    }

    public void setServiceListener(IManageServiceListener listener){
        if (listener != null) {
            serviceListener = listener;
        }
    }

    public void saveCurrentState(Context context) {
        if (model != null) {
            model.saveCurrentState(new Preference(context));
        }
    }
}
