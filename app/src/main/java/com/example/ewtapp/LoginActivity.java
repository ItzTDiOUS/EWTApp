package com.example.ewtapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, signupButton;
    private TextInputLayout usernameVar, passwordVar;

    private static final String LOGIN_PREFS = "loginPrefs";
    private static final String LOGIN_STATUS = "loginStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_in);



        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.button2);
        usernameVar = findViewById(R.id.username_box);
        passwordVar = findViewById(R.id.password_box);

        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v -> openOtpSendActivity());

    }

    private void loginUser() {
        String username = usernameVar.getEditText().getText().toString();
        String password = passwordVar.getEditText().getText().toString();

        if (username.isEmpty()) {
            usernameVar.setError("Please Enter Username");
            return;
        }

        if (password.isEmpty()) {
            passwordVar.setError("Please Enter Password");
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin");
        Query checkUsername = databaseReference.orderByChild("username").equalTo(username);

        checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordCheck = snapshot.child(username).child("password").getValue(String.class);
                    if (passwordCheck != null && passwordCheck.equals(password)) {
                        handleSuccessfulLogin();
                    } else {
                        passwordVar.setError("Incorrect Password");
                    }
                } else {
                    usernameVar.setError("Username does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void handleSuccessfulLogin() {
        usernameVar.setError(null);
        usernameVar.setErrorEnabled(false);
        passwordVar.setError(null);
        passwordVar.setErrorEnabled(false);

        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        saveLoginStatus(usernameVar.getEditText().getText().toString());

        Intent intent = new Intent(getApplicationContext(), SkipActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveLoginStatus(String status) {
        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_STATUS, status);
        editor.apply();
    }

    private void openOtpSendActivity() {
        Intent intent = new Intent(getApplicationContext(), OtpSendActivity.class);
        startActivity(intent);
        finish();
    }
}
