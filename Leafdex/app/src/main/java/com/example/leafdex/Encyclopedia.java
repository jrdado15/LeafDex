package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);

        //from camera fragment
        Intent intent = getIntent();
        String comName = intent.getStringExtra("comName");
        String sciName = intent.getStringExtra("sciName");

        ImageView enc_image = findViewById(R.id.enc_image);
        TextView enc_comName = findViewById(R.id.enc_comName);
        TextView enc_sciName = findViewById(R.id.enc_sciName);
        Button enc_back = findViewById(R.id.enc_back);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        //from firebase
        reference = FirebaseDatabase.getInstance().getReference("Plants");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                enc_comName.setText("DATA UNAVAILABLE");
                enc_sciName.setText("");
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Details details = datasnapshot.getValue(Details.class);
                    if(details.scientific_name.toLowerCase().matches(sciName.toLowerCase()  + "(.*)")) {
                        Glide.with(Encyclopedia.this).load(details.image_url).into(enc_image);
                        enc_comName.setText("Common name: " + comName);
                        enc_sciName.setText("Scientific name: " + sciName);

                        listView = findViewById(R.id.expandable_list_view);
                        initializeData(details);
                        listAdapter = new ExpandableListAdapter(Encyclopedia.this, listDataHeader, listHashMap);
                        listView.setAdapter(listAdapter);
                    }
                }
                enc_back.setVisibility(View.VISIBLE);
                enc_image.setVisibility(View.VISIBLE);
                enc_comName.setVisibility(View.VISIBLE);
                enc_sciName.setVisibility(View.VISIBLE);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        enc_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeData(Details details) {
        listDataHeader= new ArrayList<>();
        listHashMap = new HashMap<>();

        //header list
        listDataHeader.add("Characteristics");
        listDataHeader.add("Care Tips");

        //child list
        List<String> plant_chars= new ArrayList<>();
        if(!details.toxicity.isEmpty())
            plant_chars.add("Toxicity: " + details.toxicity);
        if(!details.type.isEmpty())
            plant_chars.add("Type: " + details.type);
        if(!details.lifespan.isEmpty())
            plant_chars.add("Lifespan: " + details.lifespan);
        if(!details.foliage_color.isEmpty())
            plant_chars.add("Foliage Color: " + details.foliage_color);
        if(!details.flower_color.isEmpty())
            plant_chars.add("Flower Color: " + details.flower_color);
        if(!details.flower_conspicuous.isEmpty())
            plant_chars.add("Flower Conspicuous: " + details.flower_conspicuous);
        if(!details.bloom_period.isEmpty())
            plant_chars.add("Bloom Period: " + details.bloom_period);
        if(!details.fruit_seed_color.isEmpty())
            plant_chars.add("Fruit Seed Color: " + details.fruit_seed_color);
        if(!details.fruit_seed_conspicuous.isEmpty())
            plant_chars.add("Fruit Seed Conspicuous: " + details.fruit_seed_conspicuous);
        if(!details.growth_form.isEmpty())
            plant_chars.add("Growth Form: " + details.growth_form);
        if(!details.growth_rate.isEmpty())
            plant_chars.add("Growth Rate: " + details.growth_rate);
        if(!details.shape_and_orientation.isEmpty())
            plant_chars.add("Shape and Orientation: " + details.shape_and_orientation);
        if(!details.duration.isEmpty())
            plant_chars.add("Duration: " + details.duration);
        if(!details.abcission.isEmpty())
            plant_chars.add("Abscission: " + details.abcission);
        if(!details.resprout_ability.isEmpty())
            plant_chars.add("Resprout Ability: " + details.resprout_ability);
        if(!details.height_ranges.isEmpty())
            plant_chars.add("Height Ranges: " + details.height_ranges);
        if(!details.spread_ranges.isEmpty())
            plant_chars.add("Spread Ranges: " + details.spread_ranges);
        if(!details.butterfly_type.isEmpty())
            plant_chars.add("Butterfly Type: " + details.butterfly_type);
        if(!details.climate_zones.isEmpty())
            plant_chars.add("Climate Zones: " + details.climate_zones);
        if(!details.perfume.isEmpty())
            plant_chars.add("Perfume/Fragrance: " + details.perfume);
        if(!details.edible.isEmpty())
            plant_chars.add("Edible: " + details.edible);
        if(!details.bird_attractant.isEmpty())
            plant_chars.add("Bird Attractant: " + details.bird_attractant);
        if(!details.shade_tolerance.isEmpty())
            plant_chars.add("Shade Tolerance: " + details.shade_tolerance);
        if(!details.bore_water_tolerance.isEmpty())
            plant_chars.add("Bore Water Tolerance: " + details.bore_water_tolerance);
        if(!details.drought_tolerance.isEmpty())
            plant_chars.add("Drought Tolerance: " + details.drought_tolerance);
        if(!details.frost_tolerance.isEmpty())
            plant_chars.add("Frost Tolerance: " + details.frost_tolerance);
        if(!details.greywater_tolerance.isEmpty())
            plant_chars.add("Greywater Tolerance: " + details.greywater_tolerance);

        List<String> plant_care_info= new ArrayList<>();
        if(!details.water_needs.isEmpty())
            plant_care_info.add("Water Needs: " + details.water_needs);
        if(!details.sunlight.isEmpty())
            plant_care_info.add("Sunlight: " + details.sunlight);
        if(!details.soil_type.isEmpty())
            plant_care_info.add("Soil Type: " + details.soil_type);
        if(!details.potting_suggestion.isEmpty())
            plant_care_info.add("Potting Suggestion: " + details.potting_suggestion);
        if(!details.maintenance.isEmpty())
            plant_care_info.add("Maintenance: " + details.maintenance);
        if(!details.moisture_use.isEmpty())
            plant_care_info.add("Moisture Use: " + details.moisture_use);
        if(!details.water_needs.isEmpty())
            plant_care_info.add("Water Needs: " + details.water_needs);
        plant_care_info.add("Soil pH maximum: " + details.ph_maximum);
        plant_care_info.add("Soil ph minimum: " + details.ph_minimum);
        if(details.propagated_by_bare_root.equals("Yes"))
            plant_care_info.add("Propagated by bare root: Yes");
        if(details.propagated_by_bulb.equals("Yes"))
            plant_care_info.add("Propagated by bulb: Yes");
        if(details.propagated_by_container.equals("yes"))
            plant_care_info.add("Propagated by container: Yes");
        if(details.propagated_by_corm.equals("Yes"))
            plant_care_info.add("Propagated by corm: Yes");
        if(details.propagated_by_cuttings.equals("Yes"))
            plant_care_info.add("Propagated by cuttings: Yes");
        if(details.propagated_by_seed.equals("Yes"))
            plant_care_info.add("Propagated by seed: Yes");
        if(details.propagated_by_sod.equals("Yes"))
            plant_care_info.add("Propagated by sod: Yes");
        if(details.propagated_by_sprigs.equals("Yes"))
            plant_care_info.add("Propagated by sprigs: Yes");
        if(details.propagated_by_tubers.equals("Yes"))
            plant_care_info.add("Propagated by tubers: Yes");

        listHashMap.put(listDataHeader.get(0), plant_chars);
        listHashMap.put(listDataHeader.get(1), plant_care_info);
    }
}