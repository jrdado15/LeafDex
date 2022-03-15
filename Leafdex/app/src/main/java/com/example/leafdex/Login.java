package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView register, fpassword;
    private Button login;
    private EditText email, password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.tv_register);
        register.setOnClickListener(this);

        login = (Button) findViewById(R.id.btn_signin);
        login.setOnClickListener(this);

        email = (EditText) findViewById(R.id.login_emailField);
        password = (EditText) findViewById(R.id.login_passwordField);

        fpassword = (TextView) findViewById(R.id.textView4);
        fpassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tv_register:
                startActivity(new Intent (this, Register.class));
                break;
            case R.id.btn_signin:
                loginUser();
                break;
            case R.id.textView4:
                startActivity(new Intent (this, FP.class));
                break;
        }
    }

    private void loginUser() {
        String semail = email.getText().toString().trim();
        String spassword = password.getText().toString().trim();

        if(semail.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(spassword.isEmpty()) {
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(semail, spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    startActivity(new Intent (Login.this, Home.class));
                    finish();
                } else {
                    Toast.makeText(Login.this, "Wrong credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}