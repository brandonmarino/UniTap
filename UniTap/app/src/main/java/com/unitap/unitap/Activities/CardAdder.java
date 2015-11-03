package com.unitap.unitap.Activities;

import android.os.Bundle;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.R;

public class CardAdder extends NavigationPane {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNewContentView(R.layout.activity_card_adder);
        super.onCreate(savedInstanceState);
    }

}
