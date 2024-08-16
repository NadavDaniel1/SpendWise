package com.example.spendwise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputEditText register_email;
    TextInputEditText register_password;
    MaterialButton register_BTN_register;
    ContentLoadingProgressBar register_progress_bar;
    MaterialTextView register_login_now;
    DatabaseReference registerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setNavigationOfBackButton();

        findViews();
        initViews();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            transactToMainActivity();
        }
    }

    private void setNavigationOfBackButton(){
        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void transactToMainActivity() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_BTN_register = findViewById(R.id.register_BTN_register);
        register_progress_bar = findViewById(R.id.register_progress_bar);
        register_login_now = findViewById(R.id.register_login_now);
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        register_BTN_register.setOnClickListener(v-> registerData());
        register_login_now.setOnClickListener(v -> loginNow());
    }

    private void loginNow() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerData() {
        register_progress_bar.setVisibility(View.VISIBLE);
        String email, password;
        email = String.valueOf(register_email.getText());
        password = String.valueOf(register_password.getText());

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount(email, password);
    }

    private void insertRegisterDataToDB() {
        String id = FirebaseAuth.getInstance().getUid();
        if(id != null)
        registerDatabase = FirebaseDatabase.getInstance().getReference().child("SpendWise").child(id);
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        register_progress_bar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            insertRegisterDataToDB();
                            Toast.makeText(RegisterActivity.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                                    loginNow();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}
