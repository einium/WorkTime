package ru.einium.worktime.view;

import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.content.res.ColorStateList;
import ru.einium.worktime.databinding.ActivitySettingBinding;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import ru.einium.worktime.R;
import ru.einium.worktime.model.AppPreference;
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
        setting.showNotification.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b != null) {
                    binding.chbShowNotification.setChecked(b);
                }
            }
        });
        setting.closeAppOnReset.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b != null) {
                    binding.chbCloseApp.setChecked(b);
                }
            }
        });
        setting.monday_s.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i != null) {
                    binding.tvMondayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
                }
            }
        });
        setting.tuesday_s.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i != null) {
                    binding.tvTuesdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
                }
            }
        });
        setting.wednesday_s.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i != null) {
                    binding.tvWednesdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
                }
            }
        });
        setting.thursday_s.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i != null) {
                    binding.tvThursdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
                }
            }
        });
        setting.friday_s.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i != null) {
                    binding.tvFridayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
                }
            }
        });
        setting.saturday_s.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i != null) {
                    binding.tvSaturdayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
                }
            }
        });
        setting.sunday_s.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i != null) {
                    binding.tvSundayValue.setText(TimeFormatUtils.convertTimeToStringCorrectly(i*1000));
                }
            }
        });
    }

    private void addClickListeners() {
        Log.d("logtag", "            addClickListeners()");
        binding.chbShowNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setting.setShowNotification(isChecked);
            }
        });
        binding.chbCloseApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setting.setCloseAppOnReset(isChecked);
            }
        });
        binding.tvMondayValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDay = TimeFormatUtils.DayOfWeek.monday;
                showTimePickerDialog();
            }
        });
        binding.tvTuesdayValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDay = TimeFormatUtils.DayOfWeek.tuesday;
                showTimePickerDialog();
            }
        });
        binding.tvWednesdayValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDay = TimeFormatUtils.DayOfWeek.wednesday;
                showTimePickerDialog();
            }
        });
        binding.tvThursdayValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDay = TimeFormatUtils.DayOfWeek.thursday;
                showTimePickerDialog();
            }
        });
        binding.tvFridayValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDay = TimeFormatUtils.DayOfWeek.friday;
                showTimePickerDialog();
            }
        });
        binding.tvSaturdayValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDay = TimeFormatUtils.DayOfWeek.saturday;
                showTimePickerDialog();
            }
        });
        binding.tvSundayValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDay = TimeFormatUtils.DayOfWeek.sunday;
                showTimePickerDialog();
            }
        });
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
                    return TimeFormatUtils.getHoursInTime(setting.getMondayInSec());
                case tuesday:
                    return TimeFormatUtils.getHoursInTime(setting.getTuesdayInSec());
                case wednesday:
                    return TimeFormatUtils.getHoursInTime(setting.getWednesdayInSec());
                case thursday:
                    return TimeFormatUtils.getHoursInTime(setting.getThursdayInSec());
                case friday:
                    return TimeFormatUtils.getHoursInTime(setting.getFridayInSec());
                case saturday:
                    return TimeFormatUtils.getHoursInTime(setting.getSaturdayInSec());
                case sunday:
                    return TimeFormatUtils.getHoursInTime(setting.getSundayInSec());
            }
        }
        return 0;
    }

    private int getMinutesByDay(TimeFormatUtils.DayOfWeek day) {
        if (day != null) {
            switch (day) {
                case monday:
                    return TimeFormatUtils.getMinutesInTime(setting.getMondayInSec());
                case tuesday:
                    return TimeFormatUtils.getMinutesInTime(setting.getTuesdayInSec());
                case wednesday:
                    return TimeFormatUtils.getMinutesInTime(setting.getWednesdayInSec());
                case thursday:
                    return TimeFormatUtils.getMinutesInTime(setting.getThursdayInSec());
                case friday:
                    return TimeFormatUtils.getMinutesInTime(setting.getFridayInSec());
                case saturday:
                    return TimeFormatUtils.getMinutesInTime(setting.getSaturdayInSec());
                case sunday:
                    return TimeFormatUtils.getMinutesInTime(setting.getSundayInSec());
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
}
