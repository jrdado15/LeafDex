package com.example.leafdex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.leafdex.fragments.home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Welcome extends AppCompatActivity {
    Handler h = new Handler();

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();

                if (mAuth.getCurrentUser() != null) {
                    Intent i = new Intent(Welcome.this, Home.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(Welcome.this, Login.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);

    }

}