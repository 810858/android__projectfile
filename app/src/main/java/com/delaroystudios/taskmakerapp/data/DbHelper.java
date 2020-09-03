package com.delaroystudios.taskmakerapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.delaroystudios.taskmakerapp.NewTaskActivity;
import com.delaroystudios.taskmakerapp.R;
import com.delaroystudios.taskmakerapp.data.TaskContract.TaskColumns;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
NewTaskActivity n=new NewTaskActivity();
    private static final String SQL_CREATE_TABLE_TASKS = String.format("CREATE TABLE %s"
                    +" (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER)",
            TaskContract.TABLE_TASKS,
            TaskColumns._ID,
            TaskColumns.DESCRIPTION,
            TaskColumns.IS_COMPLETE,
            TaskColumns.IS_PRIORITY,
            TaskColumns.DUE_DATE
    );

    private final Context mContext;
public Context ontext;
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASKS);
        loadDemoTask(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TABLE_TASKS);
        onCreate(db);
    }

    private void loadDemoTask(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TaskColumns.DESCRIPTION, mContext.getResources().getString(R.string.demo_task));
        values.put(TaskColumns.IS_COMPLETE, 0);
        values.put(TaskColumns.IS_PRIORITY, 1);
        values.put(TaskColumns.DUE_DATE, Long.MAX_VALUE);

        db.insertOrThrow(TaskContract.TABLE_TASKS, null, values);
    }
    public boolean updateTask(String co)
    {  SQLiteDatabase dbc=this.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(TaskColumns.DESCRIPTION, mContext.getResources().getString(R.string.demo_task));

        values.put(TaskColumns.IS_COMPLETE, 0);
        values.put(TaskColumns.IS_PRIORITY, 1);
        values.put(TaskColumns.DUE_DATE, Long.MAX_VALUE);
        dbc.update(TaskContract.TABLE_TASKS,values,"TaskColumns.DESCRIPTION=?",new String[]{TaskColumns.DESCRIPTION});
        return true;

    }
}

