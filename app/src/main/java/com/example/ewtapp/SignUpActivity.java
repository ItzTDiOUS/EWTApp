package com.example.ewtapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private Button loginButton, signupButton;

    private TextInputLayout usernameInput, passwordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_up);



        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.signupbtn);
        usernameInput = findViewById(R.id.username_box);
        passwordInput = findViewById(R.id.password_box);
        confirmPasswordInput = findViewById(R.id.confirm_password_box);

        signupButton.setOnClickListener(v -> signUpUser());
        loginButton.setOnClickListener(v -> openLoginActivity());
    }

    private void signUpUser() {
        String Phone_no = getIntent().getStringExtra("Phone_no");
        String username = usernameInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();
        String confirmPassword = confirmPasswordInput.getEditText().getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals(confirmPassword)) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference("Admin");

            UserData userData = new UserData(Phone_no,username, password,"10","8");

            // Use push() to generate a unique key for each user
            reference.child(username).setValue(userData);

            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            confirmPasswordInput.setError("Passwords do not match");
        }
    }

    private void openLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
