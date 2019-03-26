package ru.einium.worktime.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import ru.einium.worktime.Service.IManageServiceListener;
import ru.einium.worktime.model.TimePreference;
import ru.einium.worktime.model.WorkTimeModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WorkTimeViewModel extends ViewModel {
    private String defValue = "--:--:--";
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
        String time = TimeFormatUtils.convertTimeToStringCorrectly(model.getWorkDayInMillis());
        workDayText.postValue(time);
        model.addListener(new IChangeTimeListener() {
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
                Log.d("logtag", "WorkTimeViewModel OnOverTimeChange() time: " + time);
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

            @Override
            public void OnPeriodicSignalCalled() {
            }

            @Override
            public void OnPreEndSignalCalled() {
            }
        });
    }

    public void loadSavedState() {
        if (model != null) {
            if (model.getGlobalStartTime() == 0) {
                Log.d("logtag", "model.loadSavedState()");
                model.loadSavedState(new TimePreference());
                String time = TimeFormatUtils.convertTimeToStringCorrectly(model.getWorkDayInMillis());
                workDayText.postValue(time);
                if (serviceListener != null && model.isStarted) {
                    serviceListener.startService();
                }
            }
        }
    }

    public void OnClickButton() {
        Log.d("logtag", "WorkTimeViewModel OnClickButton()");
        if (!model.isStarted) {
            model.Start();
            if (serviceListener != null) {
                serviceListener.startService();
            }
        } else {
            if (!model.isPaused) {
                model.Pause();
            } else {
                model.Resume();
            }
        }
        model.saveCurrentState(new TimePreference());
    }

    private String convertTimeToString(long time, boolean setTimeZone) {
        if (time == 0) return defValue;
        String prefix = "";
        if (time < 0) {
            prefix = "- ";
            time = time*(-1);
        }
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        if (setTimeZone) formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return prefix + formatter.format(new Date(time));
    }

    public void setNewWorkTime(int hourOfDay, int minute) {
        long workTimeInMillis = hourOfDay * 3600000 + minute * 60000;
        if (model != null) {
            model.setWorkTimeInMillis(workTimeInMillis);
            model.saveCurrentState(new TimePreference());
        }
        String time = TimeFormatUtils.convertTimeToStringCorrectly(hourOfDay, minute);
        workDayText.setValue(time);
    }

    public int getStartTimeHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.getGlobalStartTime());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getStartTimeMinute() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.getGlobalStartTime());
        return calendar.get(Calendar.MINUTE);
    }

    public int getWorkDayInMillis() {
        return (int) model.getWorkDayInMillis();
    }

    public void setStartTime(int hourOfDay, int minute) {
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
            model.saveCurrentState(new TimePreference());
        }
    }

    public void resetTimer() {
        Log.d("logtag", "WorkTimeViewModel resetTimer()");
        if (model != null) {
            model.reset();
            model.saveCurrentState(new TimePreference());

            startTimeText.setValue(convertTimeToString(0, false));
            if (serviceListener != null) {
                serviceListener.stopService();
            }
        }
    }

    public void setServiceListener(IManageServiceListener listener) {
        if (listener != null) {
            serviceListener = listener;
        }
    }

    public void saveCurrentState() {
        if (model != null) {
            model.saveCurrentState(new TimePreference());
        }
    }
}
