package com.example.leafdex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private Button logoutBtn;
    private FirebaseAuth mAuth;
    private ImageView pangtest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);
        pangtest = (ImageView) findViewById(R.id.pangtest);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(Home.this, Login.class));
                finish();
            }
        });

        new LoadImage().execute("https://www.yourUrl.com/image.jpg");
        if(mAuth != null){
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();

            db.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String url = dataSnapshot.child("imageURL").getValue(String.class);
                    new LoadImage().execute(url);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {

    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            if(args.length == 1){
                Log.i("doInBack 1","length = 1 ");
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                pangtest.setImageBitmap(image);
            }
        }
    }
}