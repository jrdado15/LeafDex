package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Encyclopedia extends AppCompatActivity {

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);

        Intent intent = getIntent();
        String imageURL = intent.getStringExtra("imageURL");
        String comName = intent.getStringExtra("comName");
        String sciName = intent.getStringExtra("sciName");
        Log.d("TAG", "FROM CAMERA FRAGMENT: " + imageURL);
        Log.d("TAG", "FROM CAMERA FRAGMENT: " + comName);
        Log.d("TAG", "FROM CAMERA FRAGMENT: " + sciName);

        reference = FirebaseDatabase.getInstance().getReference("Plants");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Details details = datasnapshot.getValue(Details.class);
                    if(details.scientific_name.toLowerCase().matches(sciName.toLowerCase()  + "(.*)")) {
                        Log.d("TAG", "FROM PLANT DATABASE: " + details.common_name);
                        Log.d("TAG", "FROM PLANT DATABASE: " + details.scientific_name);
                        Log.d("TAG", "FROM PLANT DATABASE: " + details.toxicity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}