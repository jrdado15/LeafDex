package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
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
    private EditText desc_ET;
    private TextView comName_ET;
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
        comName_ET = (TextView) findViewById(R.id.tv_post_plant);
        desc_ET = (EditText) findViewById(R.id.editEditText2);
        save_button = (Button) findViewById(R.id.editButton1);
        cancel_button = (Button) findViewById(R.id.editButton2);

        mProgressDialog = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            postID = extras.getString("postID");
            Log.d("TAG", postID);
        }

        reference.child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);

                if(post != null) {
                    Glide.with(Post_edit.this).load(post.imageURL).into(plant_IV);
                    comName_ET.setText(post.comName);
                    desc_ET.setText(post.desc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        KeyListener keyListener = comName_ET.getKeyListener();
        comName_ET.setKeyListener(null);

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
        String sdesc = desc_ET.getText().toString().trim();

        if(sdesc.isEmpty()) {
            desc_ET.setError("Plant description is required!");
            desc_ET.requestFocus();
            return;
        }

        mProgressDialog.setMessage("Updating post...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        HashMap hashMap = new HashMap();
        hashMap.put("desc", sdesc);

        reference.child(postID).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mProgressDialog.dismiss();
                        Toast.makeText(Post_edit.this, "Updated successfully.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(Post_edit.this, "Failed to update.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cancelChanges() {
        finish();
    }
}