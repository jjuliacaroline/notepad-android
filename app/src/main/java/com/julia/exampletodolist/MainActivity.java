package com.julia.exampletodolist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.julia.exampletodolist.database.TaskDbHelper;
import com.julia.exampletodolist.database.todolist;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHelper = new TaskDbHelper(this);
        mTaskListView = findViewById(R.id.list_todo);
        updateUI();
    }

    //add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    //create an alert dialog to add a to do
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final EditText editText = new EditText(this);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add a new task")
                .setMessage("Write your note here")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String getText = editText.getText().toString();
                        new backgroundStuff().execute(getText);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .create();
        alertDialog.show();
        return true;
    }

    //update UI when a new note is added to the list
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(todolist.TaskEntry.TABLE, new String[]{todolist.TaskEntry._ID, todolist.TaskEntry.COL_TASK_TITLE}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(todolist.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this, R.layout.tasks, R.id.task_name, taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    //delete a task
    public void deleteTask(View view) {

        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_name);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(todolist.TaskEntry.TABLE, todolist.TaskEntry.COL_TASK_TITLE + " = ?", new String[]{task});
        db.close();
        updateUI();
    }


    //do sql work in the background so it won't slow down the UI thread
    private class backgroundStuff extends AsyncTask<String, Void, Boolean> {

        private backgroundStuff() {
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String...params) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(todolist.TaskEntry.COL_TASK_TITLE, params[0]);
            db.insertWithOnConflict(todolist.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            updateUI();
        }
    }
}

