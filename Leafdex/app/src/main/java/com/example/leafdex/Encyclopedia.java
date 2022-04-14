package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Encyclopedia extends AppCompatActivity {

    private DatabaseReference reference;
    ExpandableListAdapter listAdapter;
    ExpandableListView listView;
    List<String> listDataHeader;
    HashMap<String,List<String>> listHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);

        listView = findViewById(R.id.expandable_list_view);
        initializeData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHashMap);
        listView.setAdapter(listAdapter);

        Intent intent = getIntent();
        String imageURL = intent.getStringExtra("imageURL");
        String comName = intent.getStringExtra("comName");
        String sciName = intent.getStringExtra("sciName");
        Log.d("TAG", "FROM CAMERA FRAGMENT: " + imageURL);
        Log.d("TAG", "FROM CAMERA FRAGMENT: " + comName);
        Log.d("TAG", "FROM CAMERA FRAGMENT: " + sciName);

        reference = FirebaseDatabase.getInstance().getReference("Plants");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Details details = datasnapshot.getValue(Details.class);
                    if(details.scientific_name.toLowerCase().matches(sciName.toLowerCase()  + "(.*)")) {
                        Log.d("TAG", "FROM PLANT DATABASE: " + details.common_name);
                        Log.d("TAG", "FROM PLANT DATABASE: " + details.scientific_name);
                        Log.d("TAG", "FROM PLANT DATABASE: " + details.toxicity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeData() {
        listDataHeader= new ArrayList<>();
        listHashMap = new HashMap<>();

        //HEADER LIST
        listDataHeader.add("Characteristics");
        listDataHeader.add("Care Tips");

        //CHILD LIST
        List<String> plant_chars= new ArrayList<>();
        plant_chars.add("Toxicity:");
        plant_chars.add("Type:");
        plant_chars.add("Lifespan:");
        plant_chars.add("Foliage Color:");
        plant_chars.add("Flower Color:");
        plant_chars.add("Flower Conspicuous:");
        plant_chars.add("Bloom Period:");
        plant_chars.add("Fruit/Seed Color:");
        plant_chars.add("Fruit Seed Conspicuous:");
        plant_chars.add("Growth Form:");
        plant_chars.add("Growth Rate:");
        plant_chars.add("Shape and Orientation:");
        plant_chars.add("Duration:");
        plant_chars.add("Abscission:");
        plant_chars.add("Resprout Ability:");
        plant_chars.add("Height Ranges:");
        plant_chars.add("Spread Ranges:");
        plant_chars.add("Butterfly Type:");
        plant_chars.add("Climate Zones:");
        plant_chars.add("Perfume/Fragrance:");
        plant_chars.add("Edible:");
        plant_chars.add("Bird Attractant:");
        plant_chars.add("Shade Tolerance:");
        plant_chars.add("Bore Water Tolerance:");
        plant_chars.add("Drought Tolerance:");
        plant_chars.add("Frost Tolerance:");
        plant_chars.add("Greywater Tolerance:");

        List<String> plant_care_info= new ArrayList<>();
        plant_care_info.add("Water Needs:");
        plant_care_info.add("Sunlight:");
        plant_care_info.add("Soil Type:");
        plant_care_info.add("Potting Suggestion:");
        plant_care_info.add("Maintenance:");
        plant_care_info.add("Moisture Use:");
        plant_care_info.add("Water Needs:");
        plant_care_info.add("Soil pH maximum");
        plant_care_info.add("Soil ph minimum:");
        plant_care_info.add("Propagated by:");

        listHashMap.put(listDataHeader.get(0), plant_chars);
        listHashMap.put(listDataHeader.get(1), plant_care_info);
    }
}