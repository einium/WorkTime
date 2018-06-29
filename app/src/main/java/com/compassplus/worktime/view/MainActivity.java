package com.compassplus.worktime.view;

import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.compassplus.worktime.Preference;
import com.compassplus.worktime.R;
import com.compassplus.worktime.databinding.ActivityMainBinding;
import com.compassplus.worktime.viewmodel.WorkTimeViewModel;

public class MainActivity extends AppCompatActivity {
    private WorkTimeViewModel viewModel;
    private ActivityMainBinding binding;
    //private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(WorkTimeViewModel.class);
        binding.setViewmodel(viewModel);
        setObservers(viewModel, binding);
        viewModel.loadModelState(new Preference(this));

        //intentService = new Intent(this, TimeManagementService.class);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    startForegroundService(intentService);
        //}else{
        //    startService(intentService);
        //}
    }

    //@Override
    //protected void onDestroy() {
    //    viewModel.OnDestroyApp(new Preference(this));
    //    Log.d("logtag", "onDestroy()");
    //    super.onDestroy();
    //}

    private void setObservers(WorkTimeViewModel viewModel, final ActivityMainBinding binding){
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
    }

    public void onClickButton(View view) {
        if (viewModel != null) {
            viewModel.OnClickButton();
        }
    }

    public void changeWorkDay(View view) {
        if (viewModel != null){
            new TimePickerDialog(MainActivity.this,
                    timeSetListener,
                    viewModel.getWorkDayHours(),
                    viewModel.getWorkDayMinutes(),
                    true)
                    .show();
        }
    }

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (viewModel != null) {
                viewModel.setNewWorkTime(hourOfDay, minute);
            }
        }
    };

    public void resetTimer(View view) {
        if (viewModel != null){
            viewModel.resetTimer();
        }
    }
}


