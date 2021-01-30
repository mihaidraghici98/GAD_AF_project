package com.example.gad_af_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gad_af_project.database.AppDatabase;
import com.example.gad_af_project.vehicles.Vehicle;

public class VehicleActivity extends AppCompatActivity {

    private Vehicle mVehicleData;
    private TextView vehiclePlateNo;
    public final static String IKEY_VEHICLE_ID = "com.example.intent.keys.VEHICLE_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        vehiclePlateNo = findViewById(R.id.activityVehicle_plateno);

        Intent intent = getIntent();
        int mVehicleId = -1;
        if(intent != null){
            mVehicleId = intent.getIntExtra(IKEY_VEHICLE_ID, -1);
        }
        if(mVehicleId != -1) {
            mVehicleData = AppDatabase.getAppDatabase(getApplicationContext()).vehicleDao().getVehicleById(mVehicleId);
            vehiclePlateNo.setText(mVehicleData.getPlateNumber());
        }
        else {
            vehiclePlateNo.setText("VEHID_NOT_RECEIVED");
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
        //return super.onOptionsItemSelected(item);
    }

    public static Intent getStartingIntent(Context ctx, int vehicle_id) {
        Intent intent = new Intent(ctx, VehicleActivity.class);
        intent.putExtra(IKEY_VEHICLE_ID, vehicle_id);
        return intent;
    }
}
