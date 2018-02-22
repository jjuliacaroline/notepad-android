package com.julia.exampletodolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.julia.exampletodolist.database.TaskDbHelper;
import com.julia.exampletodolist.database.todolist;

import java.util.ArrayList;
import java.util.List;

public class TextInput extends AppCompatActivity {

    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_input_list);


        mHelper = new TaskDbHelper(this);
        mTaskListView = findViewById(R.id.list_todo);
        updateUI();

       //on long click, delete item
        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                final int pos = position;
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(view.getContext());
                deleteAlert.setMessage("Are you sure you want to delete this item?")
                        .setCancelable(false);
                deleteAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SQLiteDatabase db = mHelper.getReadableDatabase();
                        Cursor c = db.query(todolist.TaskEntry.TABLE, new String[]{todolist.TaskEntry._ID, todolist.TaskEntry.COL_TASK_TITLE}, null, null, null, null, null);
                        c.moveToPosition(pos);
                        db.delete(todolist.TaskEntry.TABLE, "_id = ?", new String[] {String.valueOf((c.getInt(c.getColumnIndex(todolist.TaskEntry._ID))))});
                        db.close();
                        updateUI();
                    }
                });
                deleteAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                deleteAlert.create();
                deleteAlert.show();

                return true;
            }
        });


    }

    //add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_text_input, menu);
        return super.onCreateOptionsMenu(menu);
    }



    //create an alert dialog to add a to do
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final EditText editText = new EditText(this);
        final AlertDialog alertDialog = new AlertDialog.Builder(TextInput.this)
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

    public void onItemClick(View v) {
        View parentRow = (View) v.getParent();
        ListView mTaskListView = (ListView) parentRow.getParent();
        final int position = mTaskListView.getPositionForView(parentRow);
        String item = (String) mTaskListView.getAdapter().getItem(position);

        Intent i = new Intent(this, listItemDetails.class);
        i.putExtra("itemInfo", item);
        startActivity(i);
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

