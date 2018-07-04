package com.compassplus.worktime.model;

import com.compassplus.worktime.viewmodel.IChangeTimeListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WorkTimeModel {
    private static WorkTimeModel currentModel;

    public static WorkTimeModel getInstance(){
        if (currentModel == null) {
            currentModel = new WorkTimeModel();
            return currentModel;
        }
        return currentModel;
    }

    private long customWorkTime = 0;
    private List<IChangeTimeListener> listeners = new ArrayList<>();

    private Timer timer;
    private TimerTask tTask;

    public boolean isStarted;
    private long globalStartTime;
    private long currentStartTime;
    private long commonWorkTime;
    private long currentWorkTime;

    public boolean isPaused;
    private long commonTimeOut;
    private long currentTimeOut;
    private long currentTimeOutStartTime;

    private WorkTimeModel() {
        isStarted = false;
        isPaused = false;
        commonWorkTime = 0;
        currentWorkTime = 0;
        currentTimeOutStartTime = 0;
        commonTimeOut = 0;
        currentTimeOut = 0;
        timer = new Timer();
    }

    public void addListener(IChangeTimeListener listener){
        listeners.add(listener);
    }

    public void removeListener(IChangeTimeListener listener){
        listeners.remove(listener);
    }

    private void notifyListeners(){
        for (IChangeTimeListener listener : listeners) {
            listener.OnStartTimeChange(globalStartTime);
            listener.OnWorkingTimeChange(commonWorkTime + currentWorkTime);
            listener.OnTimeOutChange(commonTimeOut + currentTimeOut);
            listener.OnStopTimeChange(getStopTime());
            listener.OnOverTimeChange(getOverTime());
        }
    }

    public void Start(){
        isStarted = true;
        globalStartTime = System.currentTimeMillis();
        currentStartTime = System.currentTimeMillis();

        tTask = new TimerTask() {
            public void run() {
                if (!isPaused){
                    long curTime = System.currentTimeMillis();
                    currentWorkTime = curTime - currentStartTime;
                    notifyListeners();
                } else {
                    long curTime = System.currentTimeMillis();
                    currentTimeOut = curTime-currentTimeOutStartTime;
                    notifyListeners();
                }
            }
        };
        timer.schedule(tTask, 0, 1000);
    }

    public void Pause(){
        isPaused = true;
        commonWorkTime += currentWorkTime;
        currentWorkTime = 0;
        currentTimeOutStartTime = System.currentTimeMillis();
    }

    public void Resume() {
        isPaused = false;
        commonTimeOut += currentTimeOut;
        currentTimeOut = 0;
        currentStartTime = System.currentTimeMillis();
    }

    public long getWorkDayInMillis() {
        if (customWorkTime != 0) return customWorkTime;

        int hour = 3600000;
        int minute = 60000;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
                return 8*hour + 15*minute;
            case Calendar.FRIDAY:
                return 7*hour;
            default: return 0;
        }
    }

    public void setWorkTimeInMillis(long workTimeInMillis) {
        customWorkTime = workTimeInMillis;
        notifyListeners();
    }

    private long getStopTime() {
        if (globalStartTime == 0) return 0;
        return globalStartTime + getWorkDayInMillis() + commonTimeOut + currentTimeOut;
    }

    private long getOverTime() {
        if (commonWorkTime + currentWorkTime > getWorkDayInMillis()) {
            return commonWorkTime + currentWorkTime - getWorkDayInMillis();
        }
        return 0;
    }

    public void reset() {
        isStarted = false;
        isPaused = false;
        commonWorkTime = 0;
        currentWorkTime = 0;
        globalStartTime = 0;
        currentTimeOutStartTime = 0;
        commonTimeOut = 0;
        currentTimeOut = 0;
        tTask.cancel();
        tTask = null;
        notifyListeners();
    }

    //public void OnDestroyApp(Preference pref) {
    //    if (isStarted){
    //        pref.saveCurrentState(commonWorkTime, currentStartTime, commonTimeOut, currentTimeOutStartTime, customWorkTime, isPaused);
    //    }
    //    handler.removeCallbacks(workTimeRunnable);
    //    handler.removeCallbacks(timeOutRunnable);
    //}
}
