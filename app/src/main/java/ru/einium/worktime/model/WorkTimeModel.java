package ru.einium.worktime.model;

import android.util.Log;

import ru.einium.worktime.AppPreference;
import ru.einium.worktime.viewmodel.IChangeTimeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class WorkTimeModel {
    private final Object locker = new Object();
    private static WorkTimeModel currentModel;
    public static WorkTimeModel getInstance(){
        if (currentModel == null) {
            currentModel = new WorkTimeModel();
            return currentModel;
        }
        return currentModel;
    }
    private AppPreference setting = AppPreference.getInstance();
    private long customWorkTime = 0;
    private List<IChangeTimeListener> listeners = new ArrayList<>();

    private Timer timer = new Timer();
    private TimerTask tTask;

    public boolean isStarted;
    private long globalStartTime;
    private long currentStartTime;
    private long commonWorkTime;
    private long currentWorkTime;
    public boolean isPaused;
    private long currentTimeOutStartTime;
    private long commonTimeOut;
    private long currentTimeOut;

    public WorkTimeModel() {
        isStarted = false;
        globalStartTime = 0;
        currentStartTime = 0;
        commonWorkTime = 0;
        currentWorkTime = 0;
        isPaused = false;
        currentTimeOutStartTime = 0;
        commonTimeOut = 0;
        currentTimeOut = 0;
        alreadyCalled = false;
        endAlreadyCalled = false;
    }

    public void Start() {
        isStarted = true;
        globalStartTime = System.currentTimeMillis();
        currentStartTime = System.currentTimeMillis();
        Log.d("logtag", "WorkTimeModel Start()");
        if (tTask == null) {
            tTask = createTimerTask();
        }
        timer.schedule(tTask, 0, 1000);
        notifyListeners();
    }

    public void Pause() {
        isPaused = true;
        commonWorkTime += currentWorkTime;
        currentWorkTime = 0;
        currentTimeOutStartTime = System.currentTimeMillis();
        notifyListeners();
    }

    public void Resume() {
        isPaused = false;
        commonTimeOut += currentTimeOut;
        currentTimeOut = 0;
        currentStartTime = System.currentTimeMillis();
        notifyListeners();
    }

    public long getWorkDayInMillis() {
        if (customWorkTime != 0) return customWorkTime;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                return setting.getMondayInSec()*1000L;
            case Calendar.TUESDAY:
                return setting.getTuesdayInSec()*1000L;
            case Calendar.WEDNESDAY:
                return setting.getWednesdayInSec()*1000L;
            case Calendar.THURSDAY:
                return setting.getThursdayInSec()*1000L;
            case Calendar.FRIDAY:
                return setting.getFridayInSec()*1000L;
            case Calendar.SATURDAY:
                return setting.getSaturdayInSec()*1000L;
            case Calendar.SUNDAY:
                return setting.getSundayInSec()*1000L;
            default: return 0;
        }
    }

    public void setWorkTimeInMillis(long workTimeInMillis) {
        customWorkTime = workTimeInMillis;
        notifyListeners();
    }

    public void setStartTime(long startTimeInMillis) {
        long startTimeDiff = globalStartTime - startTimeInMillis;
        globalStartTime = startTimeInMillis;
        if (isStarted) {
            if (!isPaused){
                if (commonWorkTime == 0) {
                    currentStartTime = startTimeInMillis;
                } else {
                    commonWorkTime += startTimeDiff;
                }
            } else {
                commonWorkTime += startTimeDiff;
            }
        }
        notifyListeners();
    }

    private long getStopTime() {
        if (globalStartTime == 0) return 0;
        return globalStartTime + getWorkDayInMillis() + commonTimeOut + currentTimeOut;
    }

    private long getOverTime() {
        if (globalStartTime == 0) return 0;
        if (commonWorkTime + currentWorkTime > getWorkDayInMillis()) {
            return commonWorkTime + currentWorkTime - getWorkDayInMillis();
        }
        return 0;
    }

    public void reset() {
        Log.d("logtag", "WorkTimeModel reset()");
        isStarted = false;
        globalStartTime = 0;
        currentStartTime = 0;
        commonWorkTime = 0;
        currentWorkTime = 0;
        isPaused = false;
        currentTimeOutStartTime = 0;
        commonTimeOut = 0;
        currentTimeOut = 0;

        if (tTask != null){
            tTask.cancel();
            tTask = null;
        }
        notifyListeners();
    }

    private TimerTask createTimerTask(){
        return new TimerTask() {
            public void run() {
                if (!isPaused){
                    long curTime = System.currentTimeMillis();
                    currentWorkTime = curTime - currentStartTime;
                    notifyListeners();
                    checkForPeriodicSignal();
                    checkForEndingSignal();
                } else {
                    long curTime = System.currentTimeMillis();
                    currentTimeOut = curTime-currentTimeOutStartTime;
                    notifyListeners();
                }
            }
        };
    }

    public void saveCurrentState(TimePreference pref) {
        Log.d("logtag", "WorkTimeModel saveCurrentState()");
        if (isStarted){
            pref.saveCurrentState(true,
                    globalStartTime,
                    currentStartTime,
                    commonWorkTime,
                    currentWorkTime,
                    isPaused,
                    currentTimeOutStartTime,
                    commonTimeOut,
                    currentTimeOut,
                    customWorkTime);
        } else {
            pref.saveCurrentState(false,
                    0,
                    0,
                    0,
                    0,
                    false,
                    0,
                    0,
                    0,
                    0);
        }
    }

    public void loadSavedState(TimePreference prefs) {
        Log.d("logtag", "WorkTimeModel loadSavedState()");
        globalStartTime = prefs.loadGlobalStartTime();
        boolean isNewDay = checkForNewDay(globalStartTime);
        if (!isNewDay) {
            isStarted = prefs.loadStarted();
            currentStartTime = prefs.loadCurrentStartTime();
            commonWorkTime = prefs.loadCommonWorkTime();
            currentWorkTime = prefs.loadCurrentWorkTime();
            isPaused = prefs.loadPaused();
            currentTimeOutStartTime = prefs.loadCurrentTimeOutStartTime();
            commonTimeOut = prefs.loadCommonTimeOut();
            currentTimeOut = prefs.loadCurrentTimeOut();
            customWorkTime = prefs.loadCustomWorkTime();
        } else {
            reset();
        }
        if (isStarted) {
            if (tTask == null) {
                tTask = createTimerTask();
            }
            timer.schedule(tTask, 0, 1000);
        }
        notifyListeners();
    }

    private boolean checkForNewDay(long globalStartTime) {
        DateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.getDefault());
        String savedDate = formatter.format(new Date(globalStartTime));
        String today = formatter.format(new Date(System.currentTimeMillis()));
        return !savedDate.equals(today);
    }

    public void addListener(IChangeTimeListener listener){
        listeners.add(listener);
    }

    public void removeListener(IChangeTimeListener listener){
        listeners.remove(listener);
    }

    public long getGlobalStartTime(){
        return globalStartTime;
    }

    public long getMaxAllowedStartTime() {
        return System.currentTimeMillis() - commonTimeOut;
    }

    private void notifyListeners() {
        synchronized (locker) {
            for (IChangeTimeListener listener : listeners) {
                listener.OnStartTimeChange(globalStartTime);
                listener.OnWorkingTimeChange(commonWorkTime + currentWorkTime);
                listener.OnTimeOutChange(commonTimeOut + currentTimeOut);
                if (getOverTime() == 0) {
                    listener.OnStopTimeChange(getStopTime());
                }
                listener.OnOverTimeChange(getOverTime());
                listener.OnPausedChanged(isPaused);
                listener.OnStartedChanged(isStarted);
            }
        }
    }

    private boolean alreadyCalled;
    private void checkForPeriodicSignal() {
        if (setting != null && setting.signalPeriod != null && setting.signalPeriod.getValue() != null) {
            int period_millis = setting.signalPeriod.getValue() * 60 * 1000;
            if (currentWorkTime % period_millis < 2000 && !alreadyCalled) {
                for (IChangeTimeListener listener : listeners) {
                    listener.OnPeriodicSignalCalled();
                }
                alreadyCalled = true;
            } else {
                alreadyCalled = false;
            }
        }
    }
    private boolean endAlreadyCalled;
    private void checkForEndingSignal() {
        if (setting != null && setting.endSignalPeriod != null && setting.endSignalPeriod.getValue() != null) {
            int timeBeforeEnd_millis = setting.endSignalPeriod.getValue() * 60 * 1000;

            if (getWorkDayInMillis() - (commonWorkTime + currentWorkTime) < timeBeforeEnd_millis && !endAlreadyCalled) {
                endAlreadyCalled = true;
                for (IChangeTimeListener listener : listeners) {
                    listener.OnPreEndSignalCalled();
                }
            } else {
                endAlreadyCalled = false;
            }
        }
    }
}
