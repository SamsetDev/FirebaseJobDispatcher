package com.firebase.service.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String JOB_TAG = "MyJobService";
    private FirebaseJobDispatcher mDispatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnstart).setOnClickListener(this);
        findViewById(R.id.btnstop).setOnClickListener(this);

        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.btnstart == id) {
            scheduleJob();
        } else {
            cancelJob(JOB_TAG);
        }
    }


    private void scheduleJob() {
        Job myJob = mDispatcher.newJobBuilder()
                .setService(MyJobService.class) //  your job class name
                .setTag(JOB_TAG) // define your tag name
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 60)) // trigger job every 1 min
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false) // if you want when open your app then job auto replace with new job then this flag make true
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR) // this is trigger linear time inteval like.. 1min,2min,3min,4min....
                .build();
        mDispatcher.mustSchedule(myJob);
        Toast.makeText(this, "job_scheduled", Toast.LENGTH_LONG).show();
    }

    // cancel job
    private void cancelJob(String jobTag) {
        if ("".equals(jobTag)) {
            mDispatcher.cancelAll();
        } else {
            mDispatcher.cancel(jobTag);
        }
        Toast.makeText(this, "job_cancelled", Toast.LENGTH_LONG).show();
    }
}
