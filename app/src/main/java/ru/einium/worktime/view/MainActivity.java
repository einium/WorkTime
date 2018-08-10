package ru.einium.worktime.view;

import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.einium.worktime.Service.IManageServiceListener;
import ru.einium.worktime.R;
import ru.einium.worktime.Service.TimeManagementService;
import ru.einium.worktime.databinding.ActivityMainBinding;
import ru.einium.worktime.model.AppPreference;
import ru.einium.worktime.viewmodel.TimeFormatUtils;
import ru.einium.worktime.viewmodel.WorkTimeViewModel;

public class MainActivity extends AppCompatActivity {
    public static WorkTimeViewModel viewModel;
    private ActivityMainBinding binding;
    private AppPreference setting = AppPreference.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setting.loadSetting();
        Log.d("logtag", "____________________________________________________");
        Log.d("logtag", "MainActivity onCreate()");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(WorkTimeViewModel.class);
        binding.setViewmodel(viewModel);

        setObservers();
    }

    private void setObservers() {
        viewModel.startTimeText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.isEmpty())
                    binding.tvStartTimeValue.setText(s);
            }
        });
        viewModel.workingTimeText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.isEmpty())
                    binding.tvWorkingTimeValue.setText(s);
            }
        });
        viewModel.timeOutText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.isEmpty())
                    binding.tvTimeOutValue.setText(s);
            }
        });
        viewModel.stopTimeText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.isEmpty())
                    binding.tvStopTimeValue.setText(s);
            }
        });
        viewModel.overTimeText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.isEmpty())
                    binding.tvOverTimeValue.setText(s);
            }
        });
        viewModel.workDayText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.isEmpty())
                    binding.tvDayContiniousValue.setText(s);
            }
        });
        viewModel.isPaused.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {
                if (b != null) {
                    if (b) {
                        binding.button.setText(R.string.resume);
                    } else {
                        binding.button.setText(R.string.pause);
                    }
                }
            }
        });
        viewModel.isStarted.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {
                if (b != null) {
                    if (!b) {
                        binding.button.setText(R.string.start);
                    }
                }
            }
        });
        viewModel.setServiceListener(new IManageServiceListener() {
            @Override
            public void startService() {
                Log.d("logtag", "MainActivity startTimeService()");
                startTimeService();
            }
            @Override
            public void stopService() {
                Log.d("logtag", "MainActivity stopTimeService()");
                stopTimeService();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onClickButton(View view) {
        if (viewModel != null) {
            viewModel.OnClickButton();
        }
    }

    TimePickerDialog.OnTimeSetListener workTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (viewModel != null) {
                viewModel.setNewWorkTime(hourOfDay, minute);
            }
        }
    };

    public void changeWorkDay(View view) {
        if (viewModel != null) {
            int workDay = viewModel.getWorkDayInMillis();
            new TimePickerDialog(MainActivity.this,
                    workTimeSetListener,
                    TimeFormatUtils.getHoursInTime(workDay),
                    TimeFormatUtils.getMinutesInTime(workDay),
                    true)
                    .show();
        }
    }

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (viewModel != null) {
                viewModel.setStartTime(hourOfDay, minute);
            }
        }
    };

    public void changeStartTime(View view) {
        if (viewModel != null) {
            new TimePickerDialog(MainActivity.this,
                    startTimeSetListener,
                    viewModel.getStartTimeHour(),
                    viewModel.getStartTimeMinute(),
                    true)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("logtag", "MainActivity onResume()");
        viewModel.loadSavedState();
        setButtonState();
        setDateInTitle();
        setWorkDayToView();
    }

    private void setButtonState() {
        if (viewModel.isStarted.getValue() != null && viewModel.isStarted.getValue()){
            if (viewModel.isPaused.getValue() != null && viewModel.isPaused.getValue()){
                binding.button.setText(R.string.resume);
            } else {
                binding.button.setText(R.string.pause);
            }
        } else {
            binding.button.setText(R.string.start);
        }
    }

    private void setDateInTitle() {
        String title = getResources().getString(R.string.app_name);
        title = title + " - "+ getDateInHumanFormat();
        setTitle(title);
    }

    private void setWorkDayToView() {
        int workDay = viewModel.getWorkDayInMillis();
        String workDayText = TimeFormatUtils.convertTimeToStringCorrectly(workDay);
        binding.tvDayContiniousValue.setText(workDayText);
    }

    private void startTimeService() {
        startService(new Intent(this, TimeManagementService.class));
    }

    private void stopTimeService() {
        stopService(new Intent(this, TimeManagementService.class));
    }

    public void resetTimer(View view) {
        if (viewModel != null) {
            viewModel.resetTimer();
            if (setting.isCloseAppOnReset()) {
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("logtag", "MainActivity onSaveInstanceState()");
        if (viewModel != null) {
            viewModel.saveCurrentState();
        }
    }

    public String getDateInHumanFormat() {
        DateFormat humanFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return humanFormatter.format(System.currentTimeMillis());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mi_setting) {
            Log.d("logtag", "onOptionsItemSelected: setting");
            startActivity(new Intent(this, SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}


