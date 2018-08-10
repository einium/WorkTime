package ru.einium.worktime.viewmodel;

public class TimeFormatUtils {
    public enum DayOfWeek {
        monday, tuesday, wednesday, thursday, friday, saturday, sunday
    }
    public static String convertTimeToStringCorrectly(int hourOfDay, int minute){
        if (minute < 10) {
            return String.valueOf(hourOfDay) + ":0" + String.valueOf(minute);
        }
        return String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
    }
    public static String convertTimeToStringCorrectly(long timeInMillis){
        return convertTimeToStringCorrectly((int)timeInMillis);
    }

    public static String convertTimeToStringCorrectly(int timeInMillis){
        int minute = getMinutesInTime(timeInMillis);
        int hours = getHoursInTime(timeInMillis);
        if (minute < 10) {
            return String.valueOf(hours) + ":0" + String.valueOf(minute);
        }
        return String.valueOf(hours) + ":" + String.valueOf(minute);
    }

    public static int getHoursInTime(int timeInMillis) {
        return timeInMillis / 3600000;     //3600000 - ms in 1 hour
    }

    public static int getMinutesInTime(int timeInMillis) {
        int hours = timeInMillis / 3600000;
        return (timeInMillis - hours*3600000)/60000;   //60000 - ms in 1 minute
    }

    public static int convertHoursAndMinutesToSec(int hour, int minute) {
        return hour*3600 + minute*60;
    }
}
