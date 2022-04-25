package com.example.leafdex;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Chat_users extends AppCompatActivity {

    private TextView posterName_TV;
    private EditText textbox_ET;
    private ImageButton send, camgal;
    private String message, userID, posterID, from;

    private ChatAdapter chatAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView;

    private DatabaseReference reference;
    ValueEventListener seenListener;

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
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String downloadURL;

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
        String plantName = bundle.getString("plantName");

        reference = FirebaseDatabase.getInstance().getReference();
        posterName_TV = findViewById(R.id.posterName);
        textbox_ET = findViewById(R.id.textbox);
        send = findViewById(R.id.send_btn);
        camgal = findViewById(R.id.camgal_btn);

        posterName_TV.setText(posterName);
        readChats(userID, posterID); // display chat
        seenChats(userID, posterID); // seen chat

        from = "";
        if(!plantName.equals("messages")) {
            String[] split = plantName.split("\\s+");
            if(split[1].charAt(0) == '-') {
                from = "(FROM " + split[0].toUpperCase() + " " + split[1] + ") ";
            } else {
                from = "(FROM " + split[0].toUpperCase() + " " + split[1].toUpperCase() + " " + split[2] + ") ";
            }
        }

        camgal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        // if send button is clicked
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = textbox_ET.getText().toString().trim();
                exit:
                if(!message.isEmpty()) {
                    if(checkURL(message)) {
                        Toast.makeText(Chat_users.this, "URLs are not allowed.", Toast.LENGTH_SHORT).show();
                        textbox_ET.setText("");
                        break exit;
                    }
                    if(checkFrom(message)) {
                        Toast.makeText(Chat_users.this, "Message not allowed.", Toast.LENGTH_SHORT).show();
                        textbox_ET.setText("");
                        break exit;
                    }
                    HashMap hashMap = new HashMap();
                    hashMap.put("sender", userID);
                    hashMap.put("receiver", posterID);
                    hashMap.put("message", from + message);
                    hashMap.put("seen", false);
                    reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d("TAG", "MESSAGE SENT");
                                from = "";
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

    private void seenChats(final String userID, final String posterID) {
        seenListener = reference.child("Chats").orderByChild("sender").equalTo(posterID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    i++;
                    Chat chat = datasnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(userID) && i == snapshot.getChildrenCount()) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("seen", true);
                        reference.child("Chats").child(datasnapshot.getKey()).updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.child("Chats").removeEventListener(seenListener);
    }

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
                        mProgressDialog.setMessage("Uploading image...");
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();

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
        if (isFromGallery) {
            plantPicUri = plantPicUriFromGallery;
        } else {
            plantPicUri = plantPicUriFromCamera;
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("chats/" + randomKey);
        ref.putFile(plantPicUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadURL = uri.toString();

                                HashMap hashMap = new HashMap();
                                hashMap.put("sender", userID);
                                hashMap.put("receiver", posterID);
                                hashMap.put("message", downloadURL);
                                hashMap.put("seen", false);

                                reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            mProgressDialog.dismiss();
                                            Log.d("TAG", "MESSAGE SENT");
                                        } else {
                                            mProgressDialog.dismiss();
                                            Log.d("TAG", "MESSAGE NOT SENT");
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
                        Toast.makeText(Chat_users.this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
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

    public boolean checkURL(String str) {
        try {
            new URL(str).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkFrom(String str) {
        int index = str.indexOf("FROM");
        if(index != -1) {
            return true;
        } else {
            return false;
        }
    }
}