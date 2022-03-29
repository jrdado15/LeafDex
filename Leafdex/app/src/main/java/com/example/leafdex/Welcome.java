package com.example.leafdex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity {
    Handler h = new Handler();

    FirebaseAuth mAuth;

    //Ilagay lang ang mga kailangan ilagay
    private String[] PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askMultiplePermissions(requestMultiplePermission);
    }

    private void askMultiplePermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher) {
        if (!hasPermissions(PERMISSIONS)) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionLauncher.launch(PERMISSIONS);
        } else {
            GotoHomeActivity();
            Log.d("PERMISSIONS", "All permissions are already granted");
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission request not granted " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    ActivityResultLauncher<String> requestSinglePermission = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(),
        new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Log.d("PERMISSIONS", "Permission is granted.");
                } else {
                    Log.d("PERMISSIONS", "Permission is denied.");
                }
            }
        }
    );

    ActivityResultLauncher<String[]> requestMultiplePermission = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        isGranted-> {
            Log.d("PERMISSIONS", "All permissions are granted.");
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted.");
            }
        }
    );

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("lol", "requestCode: " + requestCode);
        GotoHomeActivity();
    }

    private void GotoHomeActivity(){
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