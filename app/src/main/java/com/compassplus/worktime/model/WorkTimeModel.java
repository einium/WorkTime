package com.compassplus.worktime.model;

import android.os.Handler;
import android.util.Log;

import com.compassplus.worktime.Preference;
import com.compassplus.worktime.viewmodel.WorkTimeViewModel;

import java.util.Calendar;

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
    private WorkTimeViewModel.IChangeTimeListener listener;
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

    public void loadState(Preference pref, final WorkTimeViewModel.IChangeTimeListener listener){
        this.listener = listener;
        boolean isStateSaved = pref.loadSavedState();
        if (isStateSaved) {
            Log.d("logtag", "State loaded");
            isStarted = true;
            isPaused = pref.loadPaused();
            currentStartTime = pref.loadCurrentStartTime();
            commonWorkTime = pref.loadCommonWorkTime();
            commonTimeOut = pref.loadCommonTimeOut();
            currentTimeOutStartTime = pref.loadCurrentTimeOutStartTime();
            customWorkTime = pref.loadCustomWorkTime();
            if (isPaused){
                currentWorkTime = 0;
                currentTimeOut = System.currentTimeMillis() - currentTimeOutStartTime;
            } else {
                currentWorkTime = System.currentTimeMillis() - currentStartTime;
                currentTimeOut = 0;
            }

            if (commonWorkTime + currentWorkTime > getWorkDayInMillis()){
                overTime = commonWorkTime + currentWorkTime - getWorkDayInMillis();
            }

            if (isPaused){
                handler.postDelayed(timeOutRunnable, 0);
            } else {
                handler.postDelayed(workTimeRunnable, 0);
            }
        } else {
            Log.d("logtag", "State not loaded");
            //create new state
            isStarted = false;
            isPaused = false;
            commonWorkTime = 0;
            currentWorkTime = 0;

            currentTimeOutStartTime = 0;
            commonTimeOut = 0;
            currentTimeOut = 0;

            overTime = 0;
        }

        handler = new Handler();
        createRunnables();
    }

    private void createRunnables() {
        workTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (isStarted && !isPaused) {
                    long curTime = System.currentTimeMillis();
                    currentWorkTime = curTime-currentStartTime;
                    listener.OnWorkingTimeChange(commonWorkTime + currentWorkTime);
                    if (commonWorkTime + currentWorkTime > getWorkDayInMillis()){
                        overTime = commonWorkTime + currentWorkTime - getWorkDayInMillis();
                        listener.OnOverTimeChange(overTime);
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
                    listener.OnTimeOutChange(commonTimeOut + currentTimeOut);
                    if (overTime == 0){
                        listener.OnStopTimeChange(getStopTime());
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
        listener.OnStopTimeChange(getStopTime());
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
        listener.OnWorkingTimeChange(0L);
        listener.OnOverTimeChange(0L);
        listener.OnStopTimeChange(0L);
        listener.OnTimeOutChange(0L);
    }

    //public void OnDestroyApp(Preference pref) {
    //    if (isStarted){
    //        pref.saveCurrentState(commonWorkTime, currentStartTime, commonTimeOut, currentTimeOutStartTime, customWorkTime, isPaused);
    //    }
    //    handler.removeCallbacks(workTimeRunnable);
    //    handler.removeCallbacks(timeOutRunnable);
    //}
}
