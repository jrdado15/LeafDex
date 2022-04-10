package com.example.leafdex;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leafdex.fragments.camera;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat_users extends AppCompatActivity {

    private TextView posterName_TV;
    private EditText textbox_ET;
    private ImageButton send, camgal;
    private String message, userID, posterID;

    private ChatAdapter chatAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView;

    private DatabaseReference reference;

    // CAMERA AND GALLERY
    Intent chooserIntent;
    ProgressDialog mProgressDialog;

    private Uri plantPicUriFromGallery;
    private Uri plantPicUriFromCamera;
    private Boolean isFromGallery = false;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
    };
    // CAMERA AND GALLERY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageButton back = findViewById(R.id.chatBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
        posterID = bundle.getString("posterID");
        String posterName = bundle.getString("posterName");

        reference = FirebaseDatabase.getInstance().getReference();
        posterName_TV = findViewById(R.id.posterName);
        textbox_ET = findViewById(R.id.textbox);
        send = findViewById(R.id.send_btn);
        camgal = findViewById(R.id.camgal_btn);

        posterName_TV.setText(posterName);
        readChats(userID, posterID); // display chat

        // CAMERA AND GALLERY
        camgal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
        // CAMERA AND GALLERY

        // if send button is clicked
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = textbox_ET.getText().toString().trim();
                if(!message.isEmpty()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", userID);
                    hashMap.put("receiver", posterID);
                    hashMap.put("message", message);
                    reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d("TAG", "MESSAGE SENT");
                            } else {
                                Log.d("TAG", "MESSAGE NOT SENT");
                            }
                        }
                    });
                }
                textbox_ET.setText("");
            }
        });
    }

    private void readChats(final String userID, final String posterID) {
        mChat = new ArrayList<>();
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Chat chat = datasnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(userID) && chat.getSender().equals(posterID) ||
                            chat.getReceiver().equals(posterID) && chat.getSender().equals(userID)) {
                        mChat.add(chat);
                    }
                    chatAdapter = new ChatAdapter(Chat_users.this, mChat);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
    private void status(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.child("Users").child(userID).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
    */

    // CAMERA AND GALLERY
    private void choosePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        plantPicUriFromCamera = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/DCIM/Leafdex", "plant_"+ String.valueOf(System.currentTimeMillis()) + ".jpg"));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, plantPicUriFromCamera);
        getContentResolver().notifyChange(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);

        chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {cameraIntent});

        //TODO: sa permissions ako na alahab dipende sa need
        if (hasPermissions(PERMISSIONS)) {
            activityForResult.launch(chooserIntent);
        } else {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            requestMultiplePermission.launch(PERMISSIONS);
        }
    }

    ActivityResultLauncher<Intent> activityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        mProgressDialog = new ProgressDialog(Chat_users.this);
                        mProgressDialog.setMessage("Processing image...");
                        mProgressDialog.setCancelable(false);
                        // mProgressDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    isFromGallery = true;
                                    plantPicUriFromGallery = data.getData();
                                } catch (NullPointerException e){
                                    isFromGallery = false;
                                }
                                uploadImage();
                            }
                        }).start();
                    }
                }
            }
    );

    private void uploadImage() {
        Uri plantPicUri;
        String filePath;
        if (isFromGallery) {
            plantPicUri = plantPicUriFromGallery;
            filePath = getRealPathFromURI(plantPicUri).substring(1);
        } else {
            plantPicUri = plantPicUriFromCamera;
            filePath = plantPicUri.toString().substring(8);
        }
        Log.d("TAG", filePath);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", userID);
        hashMap.put("receiver", posterID);
        hashMap.put("message", filePath);
        reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("TAG", "MESSAGE SENT");
                } else {
                    Log.d("TAG", "MESSAGE NOT SENT");
                }
            }
        });
    }

    ActivityResultLauncher<String[]> requestMultiplePermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            isGranted -> {
                if (isGranted.containsValue(false)) {
                    Log.d("PERMISSIONS", "At least one of the permissions was not granted.");
                    return;
                }
                activityForResult.launch(chooserIntent);
                Log.d("PERMISSIONS", "All permissions are granted.");
            }
    );

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

    private String getRealPathFromURI(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    // CAMERA AND GALLERY
}