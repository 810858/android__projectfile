package com.delaroystudios.taskmakerapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.delaroystudios.taskmakerapp.data.TaskContract;
import com.delaroystudios.taskmakerapp.data.TaskUpdateService;

import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;

import static com.delaroystudios.taskmakerapp.data.TaskContract.CONTENT_URI;



public class NewTaskActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        View.OnClickListener {

    //Selected due date, stored as a timestamp
    private long mDueDate = Long.MAX_VALUE;

    public TextInputEditText mDescriptionView;
    private SwitchCompat mPrioritySelect;
    public static TextView mDueDateView;

    private String mDate;

    private static final String KEY_DATE = "date_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnewtask_activity);

        mDescriptionView = (TextInputEditText) findViewById(R.id.text_input_description);
        mPrioritySelect = (SwitchCompat) findViewById(R.id.switch_priority);
        mDueDateView = (TextView) findViewById(R.id.text_date);
        View mSelectDate = findViewById(R.id.select_date);

        mSelectDate.setOnClickListener(this);
        /*    updateDateDisplay();*/

        if (savedInstanceState != null) {
            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDueDateView.setText(savedDate);
            mDate = savedDate;
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_DATE, mDueDateView.getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_save) {
            saveItem();
            return true;
        }
/*if(item.getItemId()==R.id.b)
{
    UpdateItem();
    return true;
}*/
        return super.onOptionsItemSelected(item);
    }


    /* Manage the selected date value */
   /* public void setDateSelection(long selectedTimestamp) {
        mDueDate = selectedTimestamp;
        updateDateDisplay();
    }*/

    public long getDateSelection() {
        return mDueDate;
    }
    @Override
    public void onClick(View v) {
        mDueDateView.setText("");
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getFragmentManager(), "time picker");
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        mDueDateView.setText(timeText);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
    /* Click events on Due Date */
  /*  @Override
    public void onClick(View v) {
        DatePickerFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    *//* Date set events from dialog *//*
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Set to noon on the selected day
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        setDateSelection(c.getTimeInMillis());
    }
*/
   /* private void updateDateDisplay() {
        if (getDateSelection() == Long.MAX_VALUE) {
            mDueDateView.setText(R.string.date_empty);
    } else {
        CharSequence formatted = DateUtils.getRelativeTimeSpanString(this, mDueDate);
        mDueDateView.setText(formatted);
    }
    }
*/
    private void saveItem() {
        //Insert a new item
        ContentValues values = new ContentValues(4);
        values.put(TaskContract.TaskColumns.DESCRIPTION, mDescriptionView.getText().toString());
        values.put(TaskContract.TaskColumns.IS_PRIORITY, mPrioritySelect.isChecked() ? 1 : 0);
        values.put(TaskContract.TaskColumns.IS_COMPLETE, 0);
        /*   values.put(TaskContract.TaskColumns.DUE_DATE, getDateSelection());
         */
        TaskUpdateService.insertNewTask(this, values);
        finish();

    }

}

