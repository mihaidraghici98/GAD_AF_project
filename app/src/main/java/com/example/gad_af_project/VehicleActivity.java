package com.example.gad_af_project;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gad_af_project.database.AppDatabase;
import com.example.gad_af_project.vehicles.OdometerHistory;
import com.example.gad_af_project.vehicles.Vehicle;

public class VehicleActivity extends AppCompatActivity {

    public final static String IKEY_VEHICLE_ID = "com.example.intent.keys.VEHICLE_ID";

    private int mVehicleId;
    private Vehicle mVehicleData;
    private OdometerHistory mLastOdometer;

    private ActionBar actionBar;
    private TextView mMake;
    private TextView mModel;
    private TextView mEngine;
    private TextView mVIN;
    private TextView mOdometer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        Intent intent = getIntent();
        if(intent != null){
            mVehicleId = intent.getIntExtra(IKEY_VEHICLE_ID, -1);
        }
        else {
            mVehicleId = -1;
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mMake = findViewById(R.id.tv_vehicle_data_makePlaceholder);
        mModel = findViewById(R.id.tv_vehicle_data_modelPlaceholder);
        mEngine = findViewById(R.id.tv_vehicle_data_enginePlaceholder);
        mVIN = findViewById(R.id.tv_vehicle_data_vinPlaceholder);
        mOdometer = findViewById(R.id.tv_vehicle_data_odometerPlaceholder);

        getData();
    }

    private void getData(){
        class GetData extends AsyncTask<Void, Void, Vehicle> {
            @Override
            protected Vehicle doInBackground(Void... voids) {
                if(mVehicleId == -1) {
                    actionBar.setTitle("VEHID_NOT_RECEIVED");
                    return null;
                }

                mLastOdometer = AppDatabase.getAppDatabase(null).odometerDao().getLastOdometerEntryForVehicle(mVehicleId);
                return AppDatabase.getAppDatabase(getApplicationContext()).vehicleDao().getVehicleById(mVehicleId);
            }

            @Override
            protected void onPostExecute(Vehicle vehicle) {
                super.onPostExecute(vehicle);
                if (vehicle == null)
                    return;
                mVehicleData = vehicle;
                actionBar.setTitle(mVehicleData.getPlateNumberFormatted());
                mMake.setText(mVehicleData.getMake());
                mModel.setText(mVehicleData.getModel());
                mEngine.setText(mVehicleData.getEngineCapacity() + "cmc " + mVehicleData.getEnginePower() + "hp");
                mVIN.setText(mVehicleData.getVin());

                if (mLastOdometer != null)
                    mOdometer.setText(String.valueOf(mLastOdometer.getValue()));
                else
                    mOdometer.setText("-");
            }
        }
        GetData gd = new GetData();
        gd.execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartingIntent(Context ctx, int vehicle_id) {
        Intent intent = new Intent(ctx, VehicleActivity.class);
        intent.putExtra(IKEY_VEHICLE_ID, vehicle_id);
        return intent;
    }
}
