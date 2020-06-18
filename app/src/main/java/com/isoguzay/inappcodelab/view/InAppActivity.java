package com.isoguzay.inappcodelab.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.isoguzay.inappcodelab.R;

public class InAppActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inapp);

        button = findViewById(R.id.button_get);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent back = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(back);
            }
        });
    }

}
