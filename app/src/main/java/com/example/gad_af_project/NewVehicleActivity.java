package com.example.gad_af_project;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.gad_af_project.database.AppDatabase;
import com.example.gad_af_project.vehicles.OdometerHistory;
import com.example.gad_af_project.vehicles.Vehicle;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.regex.Pattern;

public class NewVehicleActivity extends AppCompatActivity {

    private String TAG = "NewVehicleActivity_TAG";
    private ToggleButton mToggleButtonMoto;
    private ToggleButton mToggleButtonCar;
    private ToggleButton mToggleButtonTruck;

    private Button mSaveVehicle;

    private EditText mVIN;
    private EditText mPlateNumber;
    private EditText mMake;
    private EditText mModel;
    private EditText mYear;
    Spinner mUnitSpinner;
    private EditText mOdometer;
    Spinner mFuelTypeSpinner;
    EditText mFuelCapacity;
    EditText mEngineCapacity;
    EditText mEnginePower;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mToggleButtonMoto =     (ToggleButton)findViewById(R.id.newVehicle_moto);
        mToggleButtonMoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onVehicleTypeClick(v); }
        });

        mToggleButtonCar =      (ToggleButton)findViewById(R.id.newVehicle_car);
        mToggleButtonCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onVehicleTypeClick(v); }
        });

        mToggleButtonTruck =    (ToggleButton)findViewById(R.id.newVehicle_truck);
        mToggleButtonTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onVehicleTypeClick(v); }
        });
        setButtonState(getResources(), false, true, false);

        mSaveVehicle = (Button) findViewById(R.id.newVehicle_button_saveVehicle);
        mSaveVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { saveVehicle(); }
        });

        mPlateNumber = (EditText) findViewById(R.id.newVehicle_et_plateno);
        mPlateNumber.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        mVIN = (EditText) findViewById(R.id.newVehicle_et_vin);
        mVIN.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        mUnitSpinner = findViewById(R.id.newVehicle_spinner_unit);
        String[] unitsItems = new String[]{"km", "mi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, unitsItems);
        mUnitSpinner.setAdapter(adapter);

        mFuelTypeSpinner = findViewById(R.id.newVehicle_spinner_fuel);
        String[] fuelTypeItems = new String[]{"motorina", "benzina", "GPL", "electric"};
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, fuelTypeItems);
        mFuelTypeSpinner.setAdapter(adapter);


        mMake = (EditText)findViewById(R.id.newVehicle_et_make);
        mModel = (EditText)findViewById(R.id.newVehicle_et_model);
        mYear = (EditText)findViewById(R.id.newVehicle_et_year);
        mOdometer = (EditText)findViewById(R.id.newVehicle_et_odometer);
        mFuelCapacity = (EditText)findViewById(R.id.newVehicle_et_fuelTank);
        mEngineCapacity = (EditText)findViewById(R.id.newVehicle_et_engine);
        mEnginePower = (EditText)findViewById(R.id.newVehicle_et_power);

    }

    public void onVehicleTypeClick(View v) {;
        if (v.getId() == R.id.newVehicle_car)
            setButtonState(v.getResources(), false, true, false);
        else if(v.getId() == R.id.newVehicle_moto)
            setButtonState(v.getResources(), true, false, false);
        else if(v.getId() == R.id.newVehicle_truck)
            setButtonState(v.getResources(), false, false, true);
    }

    @org.jetbrains.annotations.Nullable
    private String getVehicleType(){
        if(mToggleButtonCar.isChecked())
            return "car";
        if(mToggleButtonMoto.isChecked())
            return "moto";
        if(mToggleButtonTruck.isChecked())
            return "truck";
        return null;
    }

    private boolean validatePlateNumber(){
        Pattern mPattern = Pattern.compile("([A-Z]{2}[0-9]{2}[A-Z]{3})|(B[0-9]{3}[A-Z]{3})|(B[0-9]{2}[A-Z]{3})");
        String plateno = mPlateNumber.getText().toString();
        if(plateno.isEmpty()){
            mPlateNumber.setError("Campul nu poate fi liber!");
            return false;
        }
        else if(!mPattern.matcher(plateno).matches()){
            mPlateNumber.setError("Introdu un nr. valid. Eg: B123XYZ");
            return false;
        }
        else{
            mPlateNumber.setError(null);
            return true;
        }
    }

    private void saveVehicle(){
        class WrapParams{
            public Vehicle vehicle;
            public int odometer;
            public WrapParams(Vehicle vehicle, int odometer){
                this.vehicle = vehicle;
                this.odometer = odometer;
            }
        }
        class InsertData extends AsyncTask<WrapParams, Void, Void>{
            @Override
            protected Void doInBackground(WrapParams... wrapParams) {
                Vehicle vehicle = wrapParams[0].vehicle;
                int odometer = wrapParams[0].odometer;
                Log.v(TAG, "odometer value in doInBackground: " + odometer);
                long vehicle_id = AppDatabase.getAppDatabase(null).vehicleDao().insertVehicle(vehicle);
                if(odometer > 0){
                    OdometerHistory odometerHistory = new OdometerHistory((int)vehicle_id, new Date(), odometer, vehicle.getOdometerUnit());
                    AppDatabase.getAppDatabase(null).odometerDao().insertOdometer(odometerHistory);
                }
                return null;
            }
        }
        if (!validatePlateNumber())
            return;

        Vehicle vehicle = new Vehicle.Builder(mPlateNumber.getText().toString())
                                .withVin(mVIN.getText().toString())
                                .makeAndModel(mMake.getText().toString(), mModel.getText().toString())
                                .fromYear(mYear.getText().toString().equals("") ? 0 : Integer.parseInt(mYear.getText().toString()))
                                .havingOdometerUnit(mUnitSpinner.getSelectedItem().toString())
                                .havingFuelTypeAndCapacity(mFuelTypeSpinner.getSelectedItem().toString(), mFuelCapacity.getText().toString().equals("") ? 0 : Integer.parseInt(mFuelCapacity.getText().toString()))
                                .withEngineCapacityAndPower(mEngineCapacity.getText().toString().equals("") ? 0 : Integer.parseInt(mEngineCapacity.getText().toString()),
                                        mEnginePower.getText().toString().equals("") ? 0 : Integer.parseInt(mEnginePower.getText().toString()))
                                .build();

        int odometer = mOdometer.getText().toString().equals("") ? -1 : Integer.parseInt(mOdometer.getText().toString());

        WrapParams params = new WrapParams(vehicle, odometer);
        InsertData id = new InsertData();
        id.execute(params);
        finish();
    }

    private void setButtonState(android.content.res.Resources res, boolean moto, boolean car, boolean truck){
        if((moto && car) || (car && truck) || (truck && moto)){
            throw new InvalidParameterException("received more than one true parameter");
        }
        if(moto){
            mToggleButtonMoto.setChecked(true);
            mToggleButtonMoto.setBackgroundDrawable(ResourcesCompat.getDrawable(res, R.drawable.button_moto, null));
        }
        else {
            mToggleButtonMoto.setChecked(false);
            mToggleButtonMoto.setBackgroundDrawable(ResourcesCompat.getDrawable(res, R.drawable.button_moto_unpressed, null));
        }

        if(car){
            mToggleButtonCar.setChecked(true);
            mToggleButtonCar.setBackgroundDrawable(ResourcesCompat.getDrawable(res, R.drawable.button_car, null));
        }
        else {
            mToggleButtonCar.setChecked(false);
            mToggleButtonCar.setBackgroundDrawable(ResourcesCompat.getDrawable(res, R.drawable.button_car_unpressed, null));
        }

        if(truck){
            mToggleButtonTruck.setChecked(true);
            mToggleButtonTruck.setBackgroundDrawable(ResourcesCompat.getDrawable(res, R.drawable.button_truck, null));
        }
        else{
            mToggleButtonTruck.setChecked(true);
            mToggleButtonTruck.setBackgroundDrawable(ResourcesCompat.getDrawable(res, R.drawable.button_truck_unpressed, null));
        }
    }
}
