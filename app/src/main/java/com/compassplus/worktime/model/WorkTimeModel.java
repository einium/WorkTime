package com.compassplus.worktime.model;

import android.os.Handler;
import android.util.Log;
import com.compassplus.worktime.viewmodel.WorkTimeViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private List<WorkTimeViewModel.IChangeTimeListener> listeners = new ArrayList<>();
    private Handler handler;
    private Runnable workTimeRunnable;
    private Runnable timeOutRunnable;

    public boolean isStarted;
    private long globalStartTime;
    private long currentStartTime;
    private long commonWorkTime;
    private long currentWorkTime;
    private long overTime;

    public boolean isPaused;
    private long commonTimeOut;
    private long currentTimeOut;
    private long currentTimeOutStartTime;

    public WorkTimeModel() {
        isStarted = false;
        isPaused = false;
        commonWorkTime = 0;
        currentWorkTime = 0;
        currentTimeOutStartTime = 0;
        commonTimeOut = 0;
        currentTimeOut = 0;
        overTime = 0;
        handler = new Handler();
        createRunnables();
    }

    public void addListener(WorkTimeViewModel.IChangeTimeListener listener){
        listeners.add(listener);
    }

    private void createRunnables() {
        workTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (isStarted && !isPaused) {
                    long curTime = System.currentTimeMillis();
                    currentWorkTime = curTime - currentStartTime;
                    for (WorkTimeViewModel.IChangeTimeListener listener : listeners) {
                        listener.OnWorkingTimeChange(commonWorkTime + currentWorkTime);
                    }

                    if (commonWorkTime + currentWorkTime > getWorkDayInMillis()) {
                        overTime = commonWorkTime + currentWorkTime - getWorkDayInMillis();
                        for (WorkTimeViewModel.IChangeTimeListener listener : listeners) {
                            listener.OnOverTimeChange(overTime);
                        }
                    }
                    handler.postDelayed(workTimeRunnable, 970);
                }
            }
        };
        timeOutRunnable = new Runnable() {
            @Override
            public void run() {
                if (isStarted && isPaused) {
                    long curTime = System.currentTimeMillis();
                    currentTimeOut = curTime-currentTimeOutStartTime;
                    for (WorkTimeViewModel.IChangeTimeListener listener : listeners) {
                        listener.OnTimeOutChange(commonTimeOut + currentTimeOut);
                    }
                    if (overTime == 0){
                        for (WorkTimeViewModel.IChangeTimeListener listener : listeners) {
                            listener.OnStopTimeChange(getStopTime());
                        }
                    }
                    handler.postDelayed(timeOutRunnable, 970);
                }
            }
        };
    }

    public void Start(){
        isStarted = true;
        globalStartTime = System.currentTimeMillis();
        currentStartTime = System.currentTimeMillis();
        handler.postDelayed(workTimeRunnable, 0);
    }

    public void Pause(){
        isPaused = true;
        commonWorkTime += currentWorkTime;
        currentWorkTime = 0;
        currentTimeOutStartTime = System.currentTimeMillis();
        handler.postDelayed(timeOutRunnable, 0);
    }

    public void Resume() {
        isPaused = false;
        commonTimeOut += currentTimeOut;
        currentTimeOut = 0;
        currentStartTime = System.currentTimeMillis();
        handler.postDelayed(workTimeRunnable, 0);
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
        for (WorkTimeViewModel.IChangeTimeListener listener : listeners) {
            listener.OnStopTimeChange(getStopTime());
        }
    }

    public long getStopTime(){
        return globalStartTime + getWorkDayInMillis() + commonTimeOut + currentTimeOut;
    }

    public long getStartTime() {
        return currentStartTime;
    }

    public void reset() {
        isStarted = false;
        isPaused = false;
        commonWorkTime = 0;
        currentWorkTime = 0;
        currentTimeOutStartTime = 0;
        commonTimeOut = 0;
        currentTimeOut = 0;
        overTime = 0;
        handler.removeCallbacks(workTimeRunnable);
        handler.removeCallbacks(timeOutRunnable);
        for (WorkTimeViewModel.IChangeTimeListener listener : listeners) {
            listener.OnWorkingTimeChange(0L);
            listener.OnOverTimeChange(0L);
            listener.OnStopTimeChange(0L);
            listener.OnTimeOutChange(0L);
        }
    }

    //public void OnDestroyApp(Preference pref) {
    //    if (isStarted){
    //        pref.saveCurrentState(commonWorkTime, currentStartTime, commonTimeOut, currentTimeOutStartTime, customWorkTime, isPaused);
    //    }
    //    handler.removeCallbacks(workTimeRunnable);
    //    handler.removeCallbacks(timeOutRunnable);
    //}
}
