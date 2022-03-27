package com.example.leafdex;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.leafdex.databinding.ActivityHomeBinding;
import com.example.leafdex.fragments.camera;
import com.example.leafdex.fragments.change_password;
import com.example.leafdex.fragments.encyclopedia;
import com.example.leafdex.fragments.home;
import com.example.leafdex.fragments.profile;
import com.example.leafdex.fragments.saved_posts;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;

    ActivityHomeBinding binding;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    private ImageView profilePic;
    private TextView fullname;
    private TextView email;
    private String userID;
    private String uimageURL, ufname, ulname, uemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        setUpToolbar();
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_edit_profile:
                        replaceFragment(new profile(Home.this));
                        //Toast.makeText(Home.this, "EDIT PROFILE SELECTED", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawers();
                        break;
                    case  R.id.nav_change_password:
                        replaceFragment(new change_password(Home.this));
                        drawerLayout.closeDrawers();
                        break;
                    case  R.id.nav_saved_posts:
                        replaceFragment(new saved_posts());
                        drawerLayout.closeDrawers();
                        break;
                    case  R.id.nav_logout:
                        mAuth.signOut();
                        startActivity(new Intent(Home.this, Login.class));
                        finish();
                        //Toast.makeText(Home.this, "SIGNED OUT", Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });

        profilePic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.sidebar_profilePic);
        fullname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.sidebar_fullname);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.sidebar_email);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    replaceFragment(new home());
                    break;
                case R.id.nav_bar_camera:
                    replaceFragment(new camera());
                    break;
                case R.id.nav_bar_plantEncyclopedia:
                    replaceFragment(new encyclopedia());
                    break;
            }
            return true;
        });

        replaceFragment(new home());
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
        fragmentTransaction.replace(R.id.nav_bar_framelayout, fragment );
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {

    }
}