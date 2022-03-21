package com.example.leafdex;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.leafdex.databinding.ActivityHomeBinding;
import com.example.leafdex.fragments.camera;
import com.example.leafdex.fragments.encyclopedia;
import com.example.leafdex.fragments.home;
import com.example.leafdex.fragments.profile;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    ActivityHomeBinding binding;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        setUpToolbar();
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_edit_profile:
                        replaceFragment(new profile());
                        Toast.makeText(Home.this, "EDIT PROFILE SELECTED", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawers();
                        break;
                    case  R.id.nav_saved_posts:
                        Toast.makeText(Home.this, "SAVED POSTS SELECTED", Toast.LENGTH_LONG).show();
                        break;
                    case  R.id.nav_logout:
                        mAuth.signOut();
                        startActivity(new Intent(Home.this, Login.class));
                        finish();
                        Toast.makeText(Home.this, "SIGNED OUT", Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
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