package com.example.ewtapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LimPage extends AppCompatActivity {

    private EditText editText1, editText2;
    private Button okButton1, okButton2;

    long waterLimitValue;

    long electricityLimitValue;

    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.limit_layout);

        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        okButton1 = findViewById(R.id.okButton1);
        okButton2 = findViewById(R.id.okButton2);
        done=findViewById(R.id.doneButton);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLimitsToSharedPreferences(electricityLimitValue, waterLimitValue);

                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        // Set click listener for OK button 1
        okButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the first EditText
                String electricityLimit = editText1.getText().toString();

                // Convert the text to a long value
                try {
                    electricityLimitValue = Long.parseLong(electricityLimit);
                    showToast("Electricity limit set successfully");

                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid long
                    showToast("Invalid electricity limit");
                    // You might want to show an error message or take appropriate action
                }
            }
        });

        // Set click listener for OK button 2
        okButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the second EditText
                String waterLimit = editText2.getText().toString();

                // Convert the text to a long value
                try {
                    waterLimitValue = Long.parseLong(waterLimit);
                    showToast("Water limit set successfully");

                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid long
                    showToast("Invalid water limit");
                    // You might want to show an error message or take appropriate action
                }
            }
        });

    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveLimitsToSharedPreferences(long electricitylimvalue, long waterlimvalue) {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the SharedPreferences editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save electricitylimvalue
        editor.putLong("electricitylimvalue", electricitylimvalue);

        // Save waterlimvalue
        editor.putLong("waterlimvalue", waterlimvalue);

        // Apply changes
        editor.apply();
    }
}

