package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FP extends AppCompatActivity {

    private EditText email;
    private Button reset;
    private ProgressBar pbar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fp);

        email = (EditText) findViewById(R.id.fp_emailField);
        reset = (Button) findViewById(R.id.btn_fp);
        pbar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String semail = email.getText().toString().trim();

        if(semail.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
            email.setError("Please provide valid email.");
            email.requestFocus();
            return;
        }

        pbar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(semail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    pbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(FP.this, "Check your email to reset the password.", Toast.LENGTH_LONG).show();
                } else {
                    pbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(FP.this, "Try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}