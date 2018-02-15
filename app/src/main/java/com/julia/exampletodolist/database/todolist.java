package com.julia.exampletodolist.database;


import android.provider.BaseColumns;

public class todolist {

    public static final String DB_NAME = " com.julia.exampletodolist.database ";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {

        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
    }
}