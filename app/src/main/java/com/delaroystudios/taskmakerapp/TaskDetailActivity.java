package com.delaroystudios.taskmakerapp;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.delaroystudios.taskmakerapp.data.DbHelper;
import com.delaroystudios.taskmakerapp.data.TaskContract;
import com.delaroystudios.taskmakerapp.reminders.AlarmScheduler;
import com.delaroystudios.taskmakerapp.views.DatePickerFragment;

import java.util.Calendar;


public  class TaskDetailActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor>{
  NewTaskActivity n;
 DbHelper dbHelper;
    private TextView mTaskDescription;
    private TextView mDueDate;
    String taskDescription;
    int mpriority;
    long dueDate = Long.MAX_VALUE;
    ImageView mPriority;
    Uri taskUri;
    Uri task;
   EditText er;
    private final int TASK_DETAIL_LOADER = 0;
 Context ntext;
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);
        //Task must be passed to this activity as a valid provider Uri
button=(Button)findViewById(R.id.b);
        n=new NewTaskActivity();
        dbHelper=new DbHelper(this);
        taskUri = getIntent().getData();
 er=(EditText)findViewById(R.id.edit);
        mTaskDescription = (TextView) findViewById(R.id.text_description);
        mDueDate = (TextView) findViewById(R.id.due_date);
        mPriority = (ImageView) findViewById(R.id.priority);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask();
            }
        });
        //TODO: Display attributes of the provided task in the UI
        if (taskUri != null){
            getLoaderManager().initLoader(TASK_DETAIL_LOADER, null, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reminder:
                DatePickerFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "datePicker");
                return true;
            case R.id.action_delete:
                deleteTask();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteTask() {
        if (taskUri != null) {

            int rowsDeleted = getContentResolver().delete(taskUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.deleting_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.deleting_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

        private void updateTask()
        {
            if (taskUri != null) {
                int rowsUpdated = getContentResolver().update(taskUri, null, null, null);

                if (rowsUpdated == 0) {

                    Toast.makeText(this, "task failed",
                            Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(this, "task update successfully",
                            Toast.LENGTH_SHORT).show();
                }
            }

            finish();
        }

  /*  public  void updateTas() {

        String id = er.getText().toString();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(TaskDetailActivity.this,"pls enter id",Toast.LENGTH_LONG).show();
            return;
        }
        boolean isUpdate=dbHelper.updateTask(ntext.getResources().getString(R.string.demo_task));
        if(isUpdate==true)
        {
            Toast.makeText(TaskDetailActivity.this,"data update",Toast.LENGTH_LONG).show();
            n.mDescriptionView.setText("");
        }
        else {
            Toast.makeText(TaskDetailActivity.this,"data not update",Toast.LENGTH_LONG).show();
        }

    }  */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //TODO: Handle date selection from a DatePickerFragment
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

    public void setDateSelection(long selectedTimestamp) {
        dueDate = selectedTimestamp;
        long time= System.currentTimeMillis();
        if(dueDate < time){
            Toast.makeText(this, "Task alarm cannot be set to a past date", Toast.LENGTH_SHORT).show();
            return;
        }else {
           new AlarmScheduler().scheduleAlarm(getApplicationContext(), dueDate, taskUri);
            Toast.makeText(this, "Task alarm set ", Toast.LENGTH_SHORT).show();
        }

    }

    public long getDateSelection() {
        return dueDate;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] mProjection =
                {
                        TaskContract.TaskColumns._ID,
                        TaskContract.TaskColumns.DESCRIPTION,
                        TaskContract.TaskColumns.IS_COMPLETE,
                        TaskContract.TaskColumns.IS_PRIORITY,
                        TaskContract.TaskColumns.DUE_DATE
                };

        String mSelectionClause = null;
        String[] mSelectionArgs = null;
        String mSortOrder = null;

        return new CursorLoader(this,
                taskUri,
                mProjection,
                mSelectionClause,
                mSelectionArgs,
                mSortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int taskdescription = cursor.getColumnIndex(TaskContract.TaskColumns.DESCRIPTION);
            int duedate = cursor.getColumnIndex(TaskContract.TaskColumns.DUE_DATE);
            int priority = cursor.getColumnIndex(TaskContract.TaskColumns.IS_PRIORITY);

            taskDescription = cursor.getString(taskdescription);
            dueDate = cursor.getLong(duedate);
            mpriority = cursor.getInt(priority);

            mTaskDescription.setText(taskDescription);
            if (mpriority == 0) {
                mPriority.setImageResource(R.drawable.ic_not_priority);
            } else {
                mPriority.setImageResource(R.drawable.ic_priority);
            }

            if (getDateSelection() == Long.MAX_VALUE) {
                mDueDate.setText("");
            } else {
                CharSequence formatted = DateUtils.getRelativeTimeSpanString(this, dueDate);
                mDueDate.setText("Due date: " + formatted);
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

