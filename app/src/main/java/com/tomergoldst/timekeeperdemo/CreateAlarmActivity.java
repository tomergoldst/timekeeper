package com.tomergoldst.timekeeperdemo;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.tomergoldst.timekeeper.core.TimeKeeper;
import com.tomergoldst.timekeeper.model.Alarm;
import com.tomergoldst.timekeeper.tools.DateTools;
import com.tomergoldst.timekeeper.tools.ReadableDateTools;

import java.util.Calendar;

public class CreateAlarmActivity extends AppCompatActivity implements
        TimePickerFragment.OnDialogInteractionListener,
        DatePickerFragment.OnDialogInteractionListener {

    private static final String TAG = CreateAlarmActivity.class.getSimpleName();

    private Calendar mCalendar;
    private TextView mTimeTxv;
    private TextView mDateTxv;
    private EditText mAlarmUidEdt;
    private CheckBox mActionPersistCkb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(getString(R.string.create_alarm));

        // Set initial calendar object to the next minute
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        DateTools.addMinutes(mCalendar, 1);

        // set views
        mAlarmUidEdt = findViewById(R.id.alarmUidEdt);
        mActionPersistCkb = findViewById(R.id.alarmPersistCkb);

        // set date and time views
        mTimeTxv = findViewById(R.id.timeTxv);
        mDateTxv = findViewById(R.id.dateTxv);
        setTimeText(mTimeTxv);
        setDateText(mDateTxv);

        // set buttons
        final Button createAlarmBtn = findViewById(R.id.createAlarmBtn);
        createAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateAlarmClicked();
            }
        });

        Button mSetTimeBtn = findViewById(R.id.setTimeBtn);
        mSetTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetTimeClicked();
            }
        });

        Button mSetDateBtn = findViewById(R.id.setDateBtn);
        mSetDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetDateClicked();
            }
        });
    }

    private void onSetDateClicked() {
        DialogFragment newDateFragment = DatePickerFragment.newInstance();
        newDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void onSetTimeClicked() {
        DialogFragment newTimeFragment = TimePickerFragment.newInstance(
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE)
        );
        newTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void onCreateAlarmClicked() {
        String actionText = mAlarmUidEdt.getText().toString();

        if (TextUtils.isEmpty(actionText)){
            mAlarmUidEdt.setError("UID cannot be empty");
            return;
        }

        Alarm alarm = new Alarm(actionText, mCalendar.getTime().getTime());
        alarm.setPersist(mActionPersistCkb.isChecked());
        TimeKeeper.getInstance().setAlarm(alarm);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onDateSelected(int year, int monthOfYear, int dayOfMonth) {
        DateTools.setDateTo(mCalendar, year, monthOfYear, dayOfMonth);
        setDateText(mDateTxv);
    }

    @Override
    public void onTimeSelected(int hourOfDay, int minute) {
        DateTools.setTimeTo(mCalendar, hourOfDay, minute);
        setTimeText(mTimeTxv);
    }

    private void setTimeText(TextView timeTxv) {
        timeTxv.setText(getString(R.string.start_time, ReadableDateTools.getReadableTimeText(mCalendar)));
    }

    private void setDateText(TextView dateTxb) {
        dateTxb.setText(getString(R.string.start_date, ReadableDateTools.getReadableDateText(mCalendar)));
    }

}
