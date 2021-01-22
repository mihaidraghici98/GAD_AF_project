package com.example.gad_af_project.vehicles;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gad_af_project.R;

public class VehicleActivity extends AppCompatActivity {

    private Vehicle mVehicleData;
    private TextView vehiclePlateNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        vehiclePlateNo = findViewById(R.id.activityVehicle_plateno);

        Intent intent = getIntent();
        if(intent != null){
            mVehicleData = (Vehicle) intent.getSerializableExtra(getString(R.string.VEHICLE_DETAILS_EXTRA));
        }

        vehiclePlateNo.setText(mVehicleData.getPlateNo());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
        //return super.onOptionsItemSelected(item);
    }
}
