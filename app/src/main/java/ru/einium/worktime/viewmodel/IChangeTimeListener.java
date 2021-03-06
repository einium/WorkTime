package ru.einium.worktime.viewmodel;

public interface IChangeTimeListener{
    void OnStartTimeChange(Long time);
    void OnWorkingTimeChange(Long time);
    void OnTimeOutChange(Long time);
    void OnStopTimeChange(Long time);
    void OnOverTimeChange(Long time);
    void OnStartedChanged(boolean started);
    void OnPausedChanged(boolean paused);
    void OnPeriodicSignalCalled();
    void OnPreEndSignalCalled();
}