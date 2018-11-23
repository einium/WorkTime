package ru.einium.worktime.view;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import ru.einium.worktime.databinding.ActivitySettingBinding;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import ru.einium.worktime.R;
import ru.einium.worktime.AppPreference;
import ru.einium.worktime.viewmodel.TimeFormatUtils;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private AppPreference setting;
    private TimeFormatUtils.DayOfWeek currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("logtag", "SettingActivity onCreate()");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        setting = AppPreference.getInstance();
        if (setting.needLoad()) {
            setting.loadSetting();
        }
    }

    @Override
    protected void onResume() {
        Log.d("logtag", "SettingActivity onResume()");
        super.onResume();
        setCheckBoxColor();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setObservers();
        addClickListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setCheckBoxColor() {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {
                        Color.WHITE,
                        Color.WHITE
                }
        );
        CompoundButtonCompat.setButtonTintList(binding.chbShowNotification, colorStateList);
        CompoundButtonCompat.setButtonTintList(binding.chbCloseApp, colorStateList);
    }

    private void setObservers() {
        Log.d("logtag", "            setObservers()");
        setting.showNotification.observe(this, b -> {
            if (b != null) {
                binding.chbShowNotification.setChecked(b);
            }
        });
        setting.closeAppOnReset.observe(this, b -> {
            if (b != null) {
                binding.chbCloseApp.setChecked(b);
            }
        });
        setting.monday_s.observe(this, i -> {
            if (i != null) {
                binding.tvMondayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
            }
        });
        setting.tuesday_s.observe(this, i -> {
            if (i != null) {
                binding.tvTuesdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
            }
        });
        setting.wednesday_s.observe(this, i -> {
            if (i != null) {
                binding.tvWednesdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
            }
        });
        setting.thursday_s.observe(this, i -> {
            if (i != null) {
                binding.tvThursdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
            }
        });
        setting.friday_s.observe(this, i -> {
            if (i != null) {
                binding.tvFridayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
            }
        });
        setting.saturday_s.observe(this, i -> {
            if (i != null) {
                binding.tvSaturdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
            }
        });
        setting.sunday_s.observe(this, i -> {
            if (i != null) {
                binding.tvSundayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
            }
        });
        setting.signalPeriod.observe(this, b -> {
            if (b != null) {
                binding.tvPeriodicSignalValue.setText(String.valueOf(b));
            }
        });
        setting.endSignalPeriod.observe(this, b -> {
            if (b != null) {
                binding.tvPreEndSignalValue.setText(String.valueOf(b));
            }
        });
    }

    private void addClickListeners() {
        Log.d("logtag", "            addClickListeners()");
        binding.chbShowNotification.setOnCheckedChangeListener((buttonView, isChecked) -> setting.setShowNotification(isChecked));
        binding.chbCloseApp.setOnCheckedChangeListener((buttonView, isChecked) -> setting.setCloseAppOnReset(isChecked));
        binding.tvMondayValue.setOnClickListener(v -> {
            currentDay = TimeFormatUtils.DayOfWeek.monday;
            showTimePickerDialog();
        });
        binding.tvTuesdayValue.setOnClickListener(v -> {
            currentDay = TimeFormatUtils.DayOfWeek.tuesday;
            showTimePickerDialog();
        });
        binding.tvWednesdayValue.setOnClickListener(v -> {
            currentDay = TimeFormatUtils.DayOfWeek.wednesday;
            showTimePickerDialog();
        });
        binding.tvThursdayValue.setOnClickListener(v -> {
            currentDay = TimeFormatUtils.DayOfWeek.thursday;
            showTimePickerDialog();
        });
        binding.tvFridayValue.setOnClickListener(v -> {
            currentDay = TimeFormatUtils.DayOfWeek.friday;
            showTimePickerDialog();
        });
        binding.tvSaturdayValue.setOnClickListener(v -> {
            currentDay = TimeFormatUtils.DayOfWeek.saturday;
            showTimePickerDialog();
        });
        binding.tvSundayValue.setOnClickListener(v -> {
            currentDay = TimeFormatUtils.DayOfWeek.sunday;
            showTimePickerDialog();
        });
        binding.tvPeriodicSignalValue.setOnClickListener((v)-> showPeriodicSignalValuePickerDialog(this));
        binding.tvPreEndSignalValue.setOnClickListener((v)->showEndSignalValuePickerDialog(this));
    }

    void showTimePickerDialog() {
        new TimePickerDialog(SettingActivity.this,
                timeListener,
                getHoursByDay(currentDay),
                getMinutesByDay(currentDay),
                true)
                .show();
    }

    private int getHoursByDay(TimeFormatUtils.DayOfWeek day) {
        if (day != null) {
            switch (day) {
                case monday:
                    return TimeFormatUtils.getHoursInTime(setting.getMondayInSec()*1000);
                case tuesday:
                    return TimeFormatUtils.getHoursInTime(setting.getTuesdayInSec()*1000);
                case wednesday:
                    return TimeFormatUtils.getHoursInTime(setting.getWednesdayInSec()*1000);
                case thursday:
                    return TimeFormatUtils.getHoursInTime(setting.getThursdayInSec()*1000);
                case friday:
                    return TimeFormatUtils.getHoursInTime(setting.getFridayInSec()*1000);
                case saturday:
                    return TimeFormatUtils.getHoursInTime(setting.getSaturdayInSec()*1000);
                case sunday:
                    return TimeFormatUtils.getHoursInTime(setting.getSundayInSec()*1000);
            }
        }
        return 0;
    }

    private int getMinutesByDay(TimeFormatUtils.DayOfWeek day) {
        if (day != null) {
            switch (day) {
                case monday:
                    return TimeFormatUtils.getMinutesInTime(setting.getMondayInSec()*1000);
                case tuesday:
                    return TimeFormatUtils.getMinutesInTime(setting.getTuesdayInSec()*1000);
                case wednesday:
                    return TimeFormatUtils.getMinutesInTime(setting.getWednesdayInSec()*1000);
                case thursday:
                    return TimeFormatUtils.getMinutesInTime(setting.getThursdayInSec()*1000);
                case friday:
                    return TimeFormatUtils.getMinutesInTime(setting.getFridayInSec()*1000);
                case saturday:
                    return TimeFormatUtils.getMinutesInTime(setting.getSaturdayInSec()*1000);
                case sunday:
                    return TimeFormatUtils.getMinutesInTime(setting.getSundayInSec()*1000);
            }
        }
        return 0;
    }

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d("logtag", "            onTimeSet(hourOfDay: "+hourOfDay+", minute: "+minute+") currentDay: " + currentDay);
            if (MainActivity.viewModel != null) {
                int newTime = TimeFormatUtils.convertHoursAndMinutesToSec(hourOfDay, minute);
                setNewTimeToDay(currentDay, newTime);
            }
        }
    };

    private void setNewTimeToDay(TimeFormatUtils.DayOfWeek day, int value) {
        if (day != null) {
            switch (day) {
                case monday:
                    setting.setMonday(value);
                    break;
                case tuesday:
                    setting.setTuesday(value);
                    break;
                case wednesday:
                    setting.setWednesday(value);
                    break;
                case thursday:
                    setting.setThursday(value);
                    break;
                case friday:
                    setting.setFriday(value);
                    break;
                case saturday:
                    setting.setSaturday(value);
                    break;
                case sunday:
                    setting.setSunday(value);
                    break;
            }
        }
    }

    private void showPeriodicSignalValuePickerDialog(Context context) {
        String title = getResources().getString(R.string.periodic_signal);
        int currentValue = 0;
        if (setting.signalPeriod != null && setting.signalPeriod.getValue() != null)
            currentValue = setting.signalPeriod.getValue();
        OnNumberSelectedListener listener = value -> setting.setSignalPeriod(value);
        showNumberPickerDialog(context, title, currentValue, listener);
    }
    private void showEndSignalValuePickerDialog(Context context) {
        String title = getResources().getString(R.string.pre_end_signal);
        int currentValue = 0;
        if (setting.endSignalPeriod != null && setting.endSignalPeriod.getValue() != null)
            currentValue = setting.endSignalPeriod.getValue();
        OnNumberSelectedListener listener = value -> setting.setEndSignalPeriod(value);
        showNumberPickerDialog(context, title, currentValue, listener);
    }

    private void showNumberPickerDialog(Context context, String title, int value, OnNumberSelectedListener listener){
        final Dialog numberPikerDialog = new Dialog(context);
        numberPikerDialog.setTitle(title);
        numberPikerDialog.setContentView(R.layout.number_picker);
        Button btnOk = numberPikerDialog.findViewById(R.id.btnOk);
        final NumberPicker np = numberPikerDialog.findViewById(R.id.numberPicker1);
        np.setMaxValue(120);
        np.setMinValue(0);
        np.setValue(value);
        np.setWrapSelectorWheel(true);
        btnOk.setOnClickListener(v -> {
            int newPeriod = np.getValue();
            listener.onNumberSelected(newPeriod);
            numberPikerDialog.dismiss();
        });
        numberPikerDialog.show();
    }

    private interface OnNumberSelectedListener{
        void onNumberSelected(int value);
    }
}
