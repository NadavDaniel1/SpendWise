package com.example.spendwise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputEditText login_email;
    TextInputEditText login_password;
    MaterialButton login_BTN_login;
    ContentLoadingProgressBar login_progress_bar;
    MaterialTextView login_register_now;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setNavigationOfBackButton();

        findViews();
        initViews();
        loginData();
    }

    private void setNavigationOfBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            // Finish the activity and exit
                            LoginActivity.this.finish();
                            System.exit(0);

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void findViews() {
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_progress_bar = findViewById(R.id.login_progress_bar);
        login_register_now = findViewById(R.id.login_register_now);
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        login_BTN_login.setOnClickListener(v-> loginData());
        login_register_now.setOnClickListener(v -> registerNow());
    }

    private void registerNow() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginData() {
        login_progress_bar.setVisibility(View.VISIBLE);
        String email, password;
        email = String.valueOf(login_email.getText());
        password = String.valueOf(login_password.getText());

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        signIn(email , password);

    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        login_progress_bar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Login Successful.",
                                    Toast.LENGTH_SHORT).show();
                                    transactToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void transactToMainActivity() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            transactToMainActivity();
        }
    }
}