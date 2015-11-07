package com.unitap.unitap.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.R;

public class CardAdder extends NavigationPane {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNewContentView(R.layout.activity_card_adder);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText cardText = (EditText) findViewById(R.id.card_desc);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("cardText",cardText.getText().toString());
                setResult(2, intent);
                finish();
            }
        });
    }

}
