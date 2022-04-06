package com.example.leafdex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Product_info extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        TextView plant_name = findViewById(R.id.tv_plant_name);
        String product = "Product Unavailable";
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            product  = extras.getString("product_name");
        }
        plant_name.setText(product);
    }
}