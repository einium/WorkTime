package com.compassplus.worktime.view;

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

import com.compassplus.worktime.Service.IManageServiceListener;
import com.compassplus.worktime.R;
import com.compassplus.worktime.Service.TimeManagementService;
import com.compassplus.worktime.databinding.ActivityMainBinding;
import com.compassplus.worktime.viewmodel.WorkTimeViewModel;

public class MainActivity extends AppCompatActivity {
    private WorkTimeViewModel viewModel;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("logtag", "MainActivity onCreate()");
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(WorkTimeViewModel.class);
        binding.setViewmodel(viewModel);
        setObservers(viewModel, binding);
        serviceIntent = new Intent(getBaseContext(), TimeManagementService.class);
    }

    private void setObservers(final WorkTimeViewModel viewModel, final ActivityMainBinding binding){
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
                MainActivity.this.startService(serviceIntent);
                //doBindService();
            }

            @Override
            public void stopService() {
                //doUnbindService();
                if (serviceIntent != null){
                    MainActivity.this.stopService(serviceIntent);
                }
            }
        });
    }

    public void onClickButton(View view) {
        if (viewModel != null) {
            viewModel.OnClickButton(this);
        }
    }

    public void changeWorkDay(View view) {
        if (viewModel != null) {
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
                viewModel.setNewWorkTime(hourOfDay, minute, getBaseContext());
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("logtag", "MainActivity onResume()");
        viewModel.loadSavedState(this);
    }

    public void resetTimer(View view) {
        if (viewModel != null) {
            viewModel.resetTimer(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("logtag", "MainActivity onSaveInstanceState()");
        if (viewModel != null) {
            viewModel.saveCurrentState(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("logtag", "MainActivity onDestroy()");
        //doUnbindService();
    }

    /*private TimeManagementService mBoundService;
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("logtag", "mConnection onServiceConnected()");
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            mBoundService = ((TimeManagementService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("logtag", "mConnection onServiceDisconnected()");
            // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            mBoundService = null;
        }
    };

    void doBindService() {
        Log.d("logtag", "doBindService()");
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation
        // that we know will be running in our own process (and thus
        // won't be supporting component replacement by other applications).
        bindService(new Intent(getBaseContext(), TimeManagementService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        Log.d("logtag", "doUnbindService()");
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }*/
}


