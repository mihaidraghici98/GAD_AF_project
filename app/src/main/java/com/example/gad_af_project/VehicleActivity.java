package com.example.gad_af_project;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gad_af_project.database.AppDatabase;
import com.example.gad_af_project.vehicles.OdometerHistory;
import com.example.gad_af_project.vehicles.Vehicle;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VehicleActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String IKEY_VEHICLE_ID = "com.example.intent.keys.VEHICLE_ID";
    Map<String, Integer> vehicleIcons;

    private int mVehicleId;
    private Vehicle mVehicleData;
    private OdometerHistory mLastOdometer;

    private ActionBar actionBar;
    private ImageView mVehicleIcon;
    private TextView mPlate;
    private TextView mMake;
    private TextView mModel;
    private TextView mEngine;
    private TextView mVIN;
    private TextView mOdometer;

    // AOE = AddOdometerEntry
    private View mEntryPointAOE;
    private View mViewAOE;
    private Button mConfirmAOE;
    private Button mCancelAOE;
    private TextView mDateAOE;

    private Button mAddDocument;

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

        mAddDocument = findViewById(R.id.bt_vehicle_addDocument);
        mAddDocument.setOnClickListener(this);

        vehicleIcons = new HashMap<String, Integer>();
        vehicleIcons.put("moto", R.drawable.button_moto);
        vehicleIcons.put("car", R.drawable.button_car);
        vehicleIcons.put("truck", R.drawable.button_truck);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Garage");

        mVehicleIcon = findViewById(R.id.iv_vehicle_icon);
        mPlate = findViewById(R.id.tv_vehicle_plateNumber);
        mMake = findViewById(R.id.tv_vehicle_data_makePlaceholder);
        mModel = findViewById(R.id.tv_vehicle_data_modelPlaceholder);
        mEngine = findViewById(R.id.tv_vehicle_data_enginePlaceholder);
        mVIN = findViewById(R.id.tv_vehicle_data_vinPlaceholder);
        mOdometer = findViewById(R.id.tv_vehicle_data_odometerPlaceholder);


        mEntryPointAOE = findViewById(R.id.ep_vehicle_addNewOdometerEntry);
        mEntryPointAOE.setOnClickListener(this);
        mViewAOE = findViewById(R.id.view_vehicle_addNewOdometerEntry);
        mViewAOE.setVisibility(View.GONE);
        resetAOEfields();
        mConfirmAOE = findViewById(R.id.bt_vehicle_confirmAddOdometerEntry);
        mConfirmAOE.setOnClickListener(this);
        mCancelAOE = findViewById(R.id.bt_vehicle_cancelAddOdometerEntry);
        mCancelAOE.setOnClickListener(this);
        mDateAOE = findViewById(R.id.tv_vehicle_AddNewOdometerEntry_datePlaceholder);
        mDateAOE.setOnClickListener(this);

        getData();
    }

    private void resetAOEfields(){
        EditText mValueET = (EditText)mViewAOE.findViewById(R.id.et_vehicle_addOdometerEntry_value);
        mValueET.setText("");
        mValueET.setError(null);

        TextView mDateTV = (TextView)mViewAOE.findViewById(R.id.tv_vehicle_AddNewOdometerEntry_datePlaceholder);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        mDateTV.setText(formatter.format(new Date()));
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
            protected void onPostExecute(@NotNull Vehicle vehicle) {
                super.onPostExecute(vehicle);
                mVehicleData = vehicle;
                mVehicleIcon.setImageDrawable(getDrawable(vehicleIcons.get(mVehicleData.getVehicleType())));
                mPlate.setText(mVehicleData.getPlateNumberFormatted());
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


    private void toggleAOEView(){
        if(mViewAOE.getVisibility() == View.GONE)
            mViewAOE.setVisibility(View.VISIBLE);
        else
            mViewAOE.setVisibility(View.GONE);
    }

    private void cancelAOEView(){
        mViewAOE.setVisibility(View.GONE);
        resetAOEfields();
    }


    static class AddOdometerEntry extends AsyncTask<Void, Void, Void>{
        WeakReference<VehicleActivity> mWeakActivity;
        OdometerHistory odometerHistory;
        public AddOdometerEntry(VehicleActivity activity, OdometerHistory odometerHistory){
            super();
            this.mWeakActivity = new WeakReference<>(activity);
            this.odometerHistory = odometerHistory;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase.getAppDatabase(null).odometerDao().insertOdometer(this.odometerHistory);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(mWeakActivity.get(), mWeakActivity.get().getResources().getString(R.string.vehicle_addOdometerEntryConfirmation), Toast.LENGTH_LONG).show();
            mWeakActivity.get().getData();
        }
    }
    private void confirmAOEView(){
        EditText mValueET = (EditText)mViewAOE.findViewById(R.id.et_vehicle_addOdometerEntry_value);
        String stringContent =  mValueET.getText().toString();
        int value;
        if(stringContent.isEmpty()){
            mValueET.setError(getResources().getString(R.string.error_emptyField));
            return;
        }
        try{
            value = Integer.parseInt(stringContent);
        }
        catch(NumberFormatException e){
            mValueET.setError(getResources().getString(R.string.error_invalidField));
            return;
        }

        TextView dateTV = (TextView)mViewAOE.findViewById(R.id.tv_vehicle_AddNewOdometerEntry_datePlaceholder);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date = null;
        try{
            date = format.parse(dateTV.getText().toString());
        }
        catch (ParseException e){
            Toast.makeText(this, "Invalid Date TextView content!", Toast.LENGTH_LONG).show();
            return;
        }

        OdometerHistory odometerHistory = new OdometerHistory(this.mVehicleId, date, value, this.mVehicleData.getOdometerUnit());
        AddOdometerEntry aoe = new AddOdometerEntry(this, odometerHistory);
        aoe.execute();

        mViewAOE.setVisibility(View.GONE);
        resetAOEfields();
    }

    private void chooseDate(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mDateAOE.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == mCancelAOE)
            cancelAOEView();
        else if(v == mConfirmAOE)
            confirmAOEView();
        else if (v == mEntryPointAOE)
            toggleAOEView();
        else if (v == mDateAOE)
            chooseDate();
        else
            Toast.makeText(this, Integer.toString(v.getId()) + " was pressed.", Toast.LENGTH_LONG).show();
    }
}
