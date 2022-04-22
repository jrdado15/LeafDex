package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Post_post extends AppCompatActivity {

    private String price, qty, desc, userID;
    private Uri filePathUri;

    private ImageView postIV;
    private EditText priceET, qtyET, descET;
    private TextView comNameET;
    private Button postBtn1, postBtn2;

    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        String filePath = intent.getStringExtra("filePath");
        String comName = intent.getStringExtra("comName");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        postIV = (ImageView) findViewById(R.id.postImageView);
        comNameET = (TextView) findViewById(R.id.tv_post_plant);
        priceET = (EditText) findViewById(R.id.et_post_price);
        qtyET = (EditText) findViewById(R.id.et_post_qty);
        descET = (EditText) findViewById(R.id.et_post_description);
        postBtn1 = (Button) findViewById(R.id.postButton1);
        postBtn2 = (Button) findViewById(R.id.postButton2);
        filePathUri = Uri.parse("file:///" + filePath);
        postIV.setImageURI(filePathUri);
        comNameET.setText(comName);
        KeyListener keyListener = comNameET.getKeyListener();
        comNameET.setKeyListener(null);
        postBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price = priceET.getText().toString().trim();
                qty = qtyET.getText().toString().trim();
                desc = descET.getText().toString().trim();
                if(price.isEmpty()) {
                    priceET.setError("Plant price is required!");
                    priceET.requestFocus();
                    return;
                }
                if(!isNumeric(price)) {
                    priceET.setError("Not a number.");
                    priceET.requestFocus();
                    return;
                }
                if(qty.isEmpty()) {
                    qtyET.setError("Plant quantity is required!");
                    qtyET.requestFocus();
                    return;
                }
                if(!isNumeric(qty)) {
                    qtyET.setError("Not a number.");
                    qtyET.requestFocus();
                    return;
                }
                if(desc.isEmpty()) {
                    descET.setError("Plant description is required!");
                    descET.requestFocus();
                    return;
                }
                final String randomKey = UUID.randomUUID().toString();
                StorageReference ref = storageReference.child("posts/" + randomKey);
                ProgressDialog mProgressDialog = new ProgressDialog(Post_post.this);
                mProgressDialog.setMessage("Posting...");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);
                ref.putFile(filePathUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadURL = uri.toString();
                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                        userID = user.getUid();
                                        String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
                                        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                                        String timeDate = currentTime + " - " + currentDate;
                                        com.example.leafdex.fragments.parsers.Post post = new com.example.leafdex.fragments.parsers.Post(downloadURL, comName, desc, userID, timeDate, price, qty);
                                        String key = FirebaseDatabase.getInstance().getReference("Posts").push().getKey();
                                        FirebaseDatabase.getInstance().getReference("Posts").child(key)
                                                .setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mProgressDialog.dismiss();
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(Post_post.this, "Posted successfully.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Post_post.this, Home.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(Post_post.this, "Failed to post. Please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(Post_post.this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        postBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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