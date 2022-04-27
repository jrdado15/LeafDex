package com.example.leafdex;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.leafdex.databinding.ActivityHomeBinding;
import com.example.leafdex.fragments.about;
import com.example.leafdex.fragments.camera;
import com.example.leafdex.fragments.change_password;
import com.example.leafdex.fragments.encyclopedia;
import com.example.leafdex.fragments.home;
import com.example.leafdex.fragments.profile;
import com.example.leafdex.fragments.saved_posts;
import com.example.leafdex.fragments.your_posts;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference userReference, chatReference;

    ActivityHomeBinding binding;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    ImageView messages;
    Intent chooserIntent;
    String currentFragment;

    private Uri plantPicUriFromGallery;
    private Uri plantPicUriFromCamera;
    private ImageView profilePic;
    private TextView fullname;
    private TextView email;
    private String userID;
    private String uimageURL, ufname, ulname, uemail;
    private Boolean isFromGallery = false;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
    };

    private ArrayList<ArrayList<String>> mChat1;

    public Uri getPlantPicUriFromGallery() { return plantPicUriFromGallery; }

    public Uri getPlantPicUriFromCamera() {
        return plantPicUriFromCamera;
    }

    public Boolean getIsFromGallery() {
        return isFromGallery;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        userID = user.getUid();

        setUpToolbar();
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_edit_profile:
                        if(!currentFragment.equals("profile")) {
                            replaceFragment(new profile(Home.this));
                            currentFragment = "profile";
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_change_password:
                        if(!currentFragment.equals("change_password")) {
                            replaceFragment(new change_password(Home.this));
                            currentFragment = "change_password";
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_your_posts:
                        if(!currentFragment.equals("your_posts")) {
                            replaceFragment(new your_posts());
                            currentFragment = "your_posts";
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_saved_posts:
                        if(!currentFragment.equals("saved_posts")) {
                            replaceFragment(new saved_posts());
                            currentFragment = "saved_posts";
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_about:
                        if(!currentFragment.equals("about")) {
                            replaceFragment(new about());
                            currentFragment = "about";
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        mAuth.signOut();
                        startActivity(new Intent(Home.this, Login.class));
                        finish();
                        break;
                }
                return false;
            }
        });

        messages = (ImageView) findViewById(R.id.imageView3);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, Chat_list.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        profilePic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.sidebar_profilePic);
        fullname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.sidebar_fullname);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.sidebar_email);

        userReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {
                    uimageURL = userProfile.imageURL;
                    ufname = userProfile.fname;
                    ulname = userProfile.lname;
                    uemail = userProfile.email;

                    Glide.with(Home.this).load(uimageURL).into(profilePic);
                    fullname.setText(ufname + ' ' + ulname);
                    email.setText(uemail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.nav_bar_home:
                    if(!currentFragment.equals("home")) {
                        replaceFragment(new home());
                        currentFragment = "home";
                    }
                    break;
                case R.id.nav_bar_camera:
                    choosePicture();
                    break;
                case R.id.nav_bar_plantEncyclopedia:
                    if(!currentFragment.equals("encyclopedia")) {
                        replaceFragment(new encyclopedia());
                        currentFragment = "encyclopedia";
                    }
                    break;
            }
            return true;
        });

        replaceFragment(new home());
        currentFragment = "home";
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if(extras.getString("signal").equals("back")) {
                replaceFragment(new saved_posts());
                currentFragment = "saved_posts";
            } else if(extras.getString("signal").equals("refresh")) {
                replaceFragment(new home());
                currentFragment = "home";
            } else {
                Toast.makeText(Home.this, "Plant not found. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        changeIcon();
        super.onResume();
    }

    private void changeIcon() {
        mChat1 = new ArrayList<>();
        chatReference.orderByChild("sender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    ArrayList msg = new ArrayList<>();
                    msg.add(datasnapshot.getKey());
                    msg.add(datasnapshot.child("sender").getValue().toString());
                    msg.add(datasnapshot.child("receiver").getValue().toString());
                    msg.add(datasnapshot.child("message").getValue().toString());
                    msg.add(datasnapshot.child("seen").getValue().toString());
                    mChat1.add(msg);
                }
                Collections.reverse(mChat1);

                int count = 0;
                String current = "id";
                for(ArrayList<String> msg : mChat1) {
                    if(!msg.isEmpty()) {
                        if(msg.get(2).equals(userID) && !msg.get(1).equals(current)) {
                            if(msg.get(4).equals("false")) {
                                count++;
                            }
                        }
                        current = msg.get(1);
                    }
                }

                ImageView imageView3 = findViewById(R.id.imageView3);
                if(count > 0) {
                    imageView3.setImageResource(R.drawable.ic_unseen_messages_foreground);
                } else {
                    imageView3.setImageResource(R.drawable.ic_messages_foreground);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_bar_framelayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {

    }

    private void choosePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        plantPicUriFromCamera = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/DCIM/Leafdex", "plant_"+ String.valueOf(System.currentTimeMillis()) + ".jpg"));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, plantPicUriFromCamera);
        getContentResolver().notifyChange(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);

        chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {cameraIntent});

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
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    ProgressDialog mProgressDialog = new ProgressDialog(Home.this);
                    mProgressDialog.setMessage("Processing image...");
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
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    replaceFragment(new camera(mProgressDialog));
                                    currentFragment = "camera";
                                }
                            });
                        }
                    }).start();
                }
            }
        }
    );

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
}