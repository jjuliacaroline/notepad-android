package com.julia.exampletodolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TaskDbHelper extends SQLiteOpenHelper{

    public TaskDbHelper(Context context) {
        super(context, todolist.DB_NAME, null, todolist.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        String createTable = "CREATE TABLE " + todolist.TaskEntry.TABLE + " ( " +
                todolist.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                todolist.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

        db.execSQL( createTable );

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + todolist.TaskEntry.TABLE);
        onCreate(db);
    }
}
