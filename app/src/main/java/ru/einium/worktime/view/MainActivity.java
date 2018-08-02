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
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.einium.worktime.Service.IManageServiceListener;
import ru.einium.worktime.R;
import ru.einium.worktime.databinding.ActivityMainBinding;
import ru.einium.worktime.viewmodel.WorkTimeViewModel;

public class MainActivity extends AppCompatActivity {
    private WorkTimeViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("logtag", "____________________________________________________");
        Log.d("logtag", "MainActivity onCreate()");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(WorkTimeViewModel.class);
        binding.setViewmodel(viewModel);
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
            new TimePickerDialog(MainActivity.this,
                    workTimeSetListener,
                    viewModel.getWorkDayHours(),
                    viewModel.getWorkDayMinutes(),
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
        setObservers(viewModel, binding);
        viewModel.loadSavedState();
        if (viewModel.isStarted.getValue() != null && viewModel.isStarted.getValue()){
            if (viewModel.isPaused.getValue() != null && viewModel.isPaused.getValue()){
                binding.button.setText(R.string.resume);
            } else {
                binding.button.setText(R.string.pause);
            }
        } else {
            binding.button.setText(R.string.start);
        }
        setDateInTitle();
    }

    private void setObservers(final WorkTimeViewModel viewModel, final ActivityMainBinding binding) {
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
                Log.d("logtag", "MainActivity sendBroadcastToStartService()");
                sendBroadcastToStartService();
            }
            @Override
            public void stopService() {
                Log.d("logtag", "MainActivity sendBroadcastToStopService()");
                sendBroadcastToStopService();
            }
        });
    }

    private void sendBroadcastToStartService() {
        Intent intent = new Intent("Start_worktime_service");
        sendBroadcast(intent);
    }

    private void sendBroadcastToStopService() {
        Intent intent = new Intent("Stop_worktime_service");
        sendBroadcast(intent);
    }

    private void setDateInTitle() {
        String title = getResources().getString(R.string.app_name);
        title = title + " - "+ getDateInHumanFormat();
        setTitle(title);
    }

    public void resetTimer(View view) {
        if (viewModel != null) {
            viewModel.resetTimer();
        }
        finish();
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
}


