package com.julia.exampletodolist;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class listItemDetails extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_details);

        String item;
        TextView itemDetails = findViewById(R.id.item_details);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    item = null;
                } else {
                    item = (String) extras.getString("itemInfo");
                }
        } else {
            item = (String) savedInstanceState.getSerializable("itemInfo");

        } itemDetails.setText(item);

    }

}
