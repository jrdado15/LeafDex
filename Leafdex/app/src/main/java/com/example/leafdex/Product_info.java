package com.example.leafdex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.leafdex.fragments.parsers.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Product_info extends AppCompatActivity {
    private ArrayList<String> productValues;
    private String userID, firebasePostKey, posterID, posterName;

    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        TextView plant_owner = findViewById(R.id.tv_post_owner);
        ImageView plant_image = findViewById(R.id.item_post_image);
        TextView plant_name = findViewById(R.id.tv_plant_info_name);
        TextView plant_desc = findViewById(R.id.tv_plant_post_description);
        TextView plant_price = findViewById(R.id.tv_plant_price);
        Button back_button = findViewById(R.id.back_button);
        Button bookmark_button = findViewById(R.id.btn_bookmark);
        Button bookmark_button_filled = findViewById(R.id.btn_bookmark_filled);
        Button message_button = findViewById(R.id.btn_chat_post_owner);
        productValues = new ArrayList<String>();
        String product = "Product Unavailable";

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            firebasePostKey = extras.getString("product_key");
        }

        reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Posts").child(firebasePostKey);
        Query querySP = reference.child("Saved").child(userID);
        plant_owner.setVisibility(View.INVISIBLE);
        plant_image.setVisibility(View.INVISIBLE);
        plant_name.setVisibility(View.INVISIBLE);
        plant_desc.setVisibility(View.INVISIBLE);
        plant_price.setVisibility(View.INVISIBLE);
        bookmark_button.setVisibility(View.INVISIBLE);
        message_button.setVisibility(View.INVISIBLE);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(firebasePostKey != null) {
            querySP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                        if(datasnapshot.getKey().equals(firebasePostKey)) {
                            bookmark_button.setVisibility(View.GONE);
                            bookmark_button_filled.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            bookmark_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child("Saved").child(userID).child(firebasePostKey).setValue("");
                    bookmark_button.setVisibility(View.GONE);
                    bookmark_button_filled.setVisibility(View.VISIBLE);
                    Toast.makeText(Product_info.this, "Added to saved posts.", Toast.LENGTH_LONG).show();
                }
            });

            bookmark_button_filled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child("Saved").child(userID).child(firebasePostKey).removeValue();
                    bookmark_button_filled.setVisibility(View.GONE);
                    bookmark_button.setVisibility(View.VISIBLE);
                    Toast.makeText(Product_info.this, "Removed from saved posts.", Toast.LENGTH_LONG).show();
                }
            });

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
                        productValues.add(post.price); //post plant price -- 5

                        //TODO: SET TEXT NG IBA PANG DETAILS @JUSTINE
                        Glide.with(Product_info.this).load(productValues.get(2)).into(plant_image);
                        plant_name.setText(productValues.get(0));
                        plant_desc.setText(productValues.get(1));
                        plant_price.setText("₱" + productValues.get(5));
                        posterID = productValues.get(3);

                        if(!userID.equals(posterID)) {
                            bookmark_button.setVisibility(View.VISIBLE);
                            message_button.setVisibility(View.VISIBLE);
                        }
                    } else {
                        plant_name.setText(product);
                        Log.d("POSTS", "No post found.");
                    }
                    plant_image.setVisibility(View.VISIBLE);
                    plant_name.setVisibility(View.VISIBLE);
                    plant_desc.setVisibility(View.VISIBLE);
                    plant_price.setVisibility(View.VISIBLE);

                    Query queryUser = reference.child("Users").child(posterID);

                    if(posterID != null) {
                        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if(user != null) {
                                    posterName = user.fname + " " + user.lname;
                                    plant_owner.setText(posterName);
                                }
                                plant_owner.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("USER", "No user found.");
                            }
                        });

                        message_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Product_info.this, Chat_users.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("posterID", posterID);
                                intent.putExtra("posterName", posterName);
                                startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("POSTS", "No post found.");
                }
            });
        }
    }
}