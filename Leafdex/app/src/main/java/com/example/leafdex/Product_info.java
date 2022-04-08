package com.example.leafdex;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.leafdex.fragments.parsers.Post;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Product_info extends AppCompatActivity {
    private ArrayList<String> productValues;

    private String firebasePostKey;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        TextView plant_name = findViewById(R.id.tv_plant_name);
        productValues = new ArrayList<String>();
        String product = "Product Unavailable";

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            firebasePostKey  = extras.getString("product_key");
        }

        reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Posts").child(firebasePostKey);
        plant_name.setVisibility(View.INVISIBLE);

        if(firebasePostKey != null){
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Post post = snapshot.getValue(Post.class);
                    if(post != null) {
                        productValues.add(post.comName); //post plant name -- 0
                        productValues.add(post.desc); //post plant description -- 1
                        productValues.add(post.imageURL); //post plant image -- 2
                        productValues.add(post.userID); //post user -- 3
                        productValues.add(post.dateTime); //post date and time -- 4

                        //TODO: SET TEXT NG IBA PANG DETAILS @JUSTINE
                        plant_name.setText(productValues.get(4));
                    } else {
                        plant_name.setText(product);
                        Log.d("POSTS", "No post found.");
                    }
                    plant_name.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("POSTS", "No post found.");
                }
            });
        }
    }
}