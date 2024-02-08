package com.example.ewtapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;



public class LimPage extends AppCompatActivity {

    private Button ElecOk;
    private Button WatOk;

    private TextInputLayout Elec;
    private TextInputLayout Wat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ... (other code)

        ElecOk = findViewById(R.id.okButton1);
        WatOk = findViewById(R.id.okButton2);

//        Elec = findViewById(R.id.editText1);
//        Wat = findViewById(R.id.editText2);

        ElecOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elecInput = Objects.requireNonNull(Elec.getEditText()).getText().toString().trim();
                if (!elecInput.isEmpty()) {
                    try {
                        long elecValue = Long.parseLong(elecInput);
                        returnResultToMainActivity("ELEC_VALUE", elecValue);
                    } catch (NumberFormatException e) {
                        // Handle the case where the input cannot be converted to long
                        // You may show an error message or take appropriate action
                    }
                } else {
                    // Handle the case where the input is empty
                    // You may show an error message or take appropriate action
                }
            }
        });

        WatOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String watInput = Objects.requireNonNull(Wat.getEditText()).getText().toString().trim();
                if (!watInput.isEmpty()) {
                    try {
                        long watValue = Long.parseLong(watInput);
                        returnResultToMainActivity("WAT_VALUE", watValue);
                    } catch (NumberFormatException e) {
                        // Handle the case where the input cannot be converted to long
                        // You may show an error message or take appropriate action
                    }
                } else {
                    // Handle the case where the input is empty
                    // You may show an error message or take appropriate action
                }
            }
        });
    }

    private void returnResultToMainActivity(String key, long value) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(key, value);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
