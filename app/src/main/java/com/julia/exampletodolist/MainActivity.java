package com.julia.exampletodolist;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public Button voice_btn;
    public Button text_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        voice_btn = findViewById(R.id.voice_input);
        voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent voice_intent = new Intent(v.getContext(), voiceInput.class);
               startActivity(voice_intent);
            }
        });

        text_btn = findViewById(R.id.text_input);
        text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent text_intent = new Intent(v.getContext(), TextInput.class);
                startActivity(text_intent);
            }
        });




    }

}
