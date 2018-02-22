package com.julia.exampletodolist;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.julia.exampletodolist.database.TaskDbHelper;
import com.julia.exampletodolist.database.todolist;

import java.util.ArrayList;
import java.util.Locale;

public class voiceInput extends Activity {

    private Button save_btn;
    private TextView speechTextView;
    private ImageView speech_icon_btn;
    private TaskDbHelper mHelper;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_input_screen);

        mHelper = new TaskDbHelper(this);

        speech_icon_btn = findViewById(R.id.speech_icon);
        speech_icon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        //jatka tästä crashaa for some reason
        save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechTextView = findViewById(R.id.speech_result_text);
                String memoText = speechTextView.getText().toString();

                if (memoText.equals("")) {
                    dontSaveMemoDialog(v);

                } else {
                    new backgroundDatabaseFuntions().execute(memoText);
                    Intent noteListIntent = new Intent(v.getContext(), TextInput.class);
                    //noteListIntent.putExtra("memoString", memoText);
                    startActivity(noteListIntent);
                }
            }
        });

    }

    public void dontSaveMemoDialog(View v) {
        AlertDialog.Builder noSaveDialog = new AlertDialog.Builder(v.getContext());
            noSaveDialog.setMessage("Are you sure you don't want to save your memo?")
                    .setCancelable(false);
            noSaveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(voiceInput.this, TextInput.class);
                    startActivity(i);
                }
            });
            noSaveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            noSaveDialog.create();
            noSaveDialog.show();

    }


    public void startRecording() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me a story");


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        speechTextView = findViewById(R.id.speech_result_text);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && requestCode == RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            speechTextView.setText(matches.get(0));
        }
    }

    private class backgroundDatabaseFuntions extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(todolist.TaskEntry.COL_TASK_TITLE, params[0]);
            db.insertWithOnConflict(todolist.TaskEntry.TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
}
