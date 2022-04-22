package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.leafdex.fragments.parsers.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Post_edit extends AppCompatActivity {

    private ImageView plant_IV;
    private EditText price_ET, qty_ET, desc_ET;
    private TextView comName_TV;
    private Button save_button, cancel_button;
    private String postID;

    private DatabaseReference reference;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        reference = FirebaseDatabase.getInstance().getReference("Posts");

        plant_IV = (ImageView) findViewById(R.id.editImageView);
        comName_TV = (TextView) findViewById(R.id.tv_post_plant);
        price_ET = (EditText) findViewById(R.id.edit_post_price);
        qty_ET = (EditText) findViewById(R.id.edit_post_qty);
        desc_ET = (EditText) findViewById(R.id.editEditText2);
        save_button = (Button) findViewById(R.id.editButton1);
        cancel_button = (Button) findViewById(R.id.editButton2);

        mProgressDialog = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            postID = extras.getString("postID");
        }

        reference.child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);

                if(post != null) {
                    Glide.with(Post_edit.this).load(post.imageURL).into(plant_IV);
                    comName_TV.setText(post.comName);
                    price_ET.setText(post.price);
                    qty_ET.setText(post.qty);
                    desc_ET.setText(post.desc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });
    }

    private void saveChanges() {
        String sprice = price_ET.getText().toString().trim();
        String sqty = qty_ET.getText().toString().trim();
        String sdesc = desc_ET.getText().toString().trim();

        if(sprice.isEmpty()) {
            price_ET.setError("Plant price is required!");
            price_ET.requestFocus();
            return;
        }

        if(!isNumeric(sprice)) {
            price_ET.setError("Not a number.");
            price_ET.requestFocus();
            return;
        }

        if(sqty.isEmpty()) {
            qty_ET.setError("Plant quantity is required!");
            qty_ET.requestFocus();
            return;
        }

        if(!isNumeric(sqty)) {
            qty_ET.setError("Not a number.");
            qty_ET.requestFocus();
            return;
        }

        if(sdesc.isEmpty()) {
            desc_ET.setError("Plant description is required!");
            desc_ET.requestFocus();
            return;
        }

        mProgressDialog.setMessage("Updating post...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        HashMap hashMap = new HashMap();
        hashMap.put("price", sprice);
        hashMap.put("qty", sqty);
        hashMap.put("desc", sdesc);

        reference.child(postID).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mProgressDialog.dismiss();
                        Toast.makeText(Post_edit.this, "Updated successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(Post_edit.this, "Failed to update.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelChanges() {
        finish();
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}