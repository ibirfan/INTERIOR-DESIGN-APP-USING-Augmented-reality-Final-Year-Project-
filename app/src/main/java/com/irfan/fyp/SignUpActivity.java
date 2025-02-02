package com.irfan.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(this);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {

            startActivity(new Intent(SignUpActivity.this, MenuActivity.class));
            finish();
        } else {

        }

        mAuth = FirebaseAuth.getInstance();

        Button loginButton = findViewById(R.id.login);
        EditText userName = findViewById(R.id.userName);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.pwText);
        EditText retypePassword = findViewById(R.id.signUpPwCheck);
        Button registerBtn = findViewById(R.id.register);

        registerBtn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String retypePasswordText = retypePassword.getText().toString().trim();
            String name = userName.getText().toString().trim();



            if (name.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please enter your name.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (emailText.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordText.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (retypePasswordText.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please retype your password.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordText.equals(retypePasswordText)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", name);
                            editor.apply();
                            Toast.makeText(SignUpActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}