package com.example.gad_af_project.ui.fragments.garage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gad_af_project.ui.activities.NewVehicleActivity;
import com.example.gad_af_project.R;
import com.example.gad_af_project.database.AppDatabase;
import com.example.gad_af_project.vehicles.Vehicle;
import com.example.gad_af_project.ui.activities.VehicleActivity;
import com.example.gad_af_project.vehicles.VehiclesAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class GarageFragment extends Fragment implements VehiclesAdapter.VehiclesViewHolder.OnVehicleListener {

    View root;
    private VehiclesAdapter mAdapter;
    private RecyclerView mList;
    private List<Vehicle> mVehiclesDataSource;

    private Button mAddButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_garage, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);

        // ========== Recycler View
        mList = root.findViewById(R.id.fragmentGarage_vehicleList);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        // ===========================

        mAddButton = root.findViewById(R.id.garage_button_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewVehicleActivity.class);
                startActivity(intent);
            }
        });
        getData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }


    static class GetData extends AsyncTask<Void, Void, List<Vehicle>>{
        GarageFragment mGarageFragment;
        public GetData(GarageFragment mGarageFragment){
            this.mGarageFragment = mGarageFragment;
        }

        @Override
        protected List<Vehicle> doInBackground(Void... voids) {
            return AppDatabase.getAppDatabase(null).vehicleDao().getAllVehicles();
        }

        @Override
        protected void onPostExecute(List<Vehicle> vehicles) {
            mGarageFragment.mVehiclesDataSource = vehicles;
            mGarageFragment.mAdapter = new VehiclesAdapter(mGarageFragment.mVehiclesDataSource, mGarageFragment);
            mGarageFragment.mList.setAdapter(mGarageFragment.mAdapter);
            super.onPostExecute(vehicles);
        }
    }
    private void getData() {

        GetData gd = new GetData(this);
        gd.execute();
    }

    @Override
    public void onVehicleClick(int position) {
        Vehicle mVehicleData = mVehiclesDataSource.get(position);
        Intent intent = VehicleActivity.getStartingIntent(getContext(), mVehicleData.getVehicleId());
        startActivity(intent);
    }


    static class DeleteData extends AsyncTask<Void, Void, Void>{

        GarageFragment mGarageFragment;
        Vehicle vehicle;

        public DeleteData(GarageFragment mGarageFragment, Vehicle vehicle){
            this.mGarageFragment = mGarageFragment;
            this.vehicle = vehicle;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase.getAppDatabase(null).vehicleDao().deleteVehicle(vehicle);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mGarageFragment.getData();
            mGarageFragment.displayMessage(mGarageFragment.getResources().getString(R.string.garage_deleteVehicleConfirmation));
        }
    }
    @Override
    public void onVehicleDelete(int position) {

        Vehicle mVehicleData = mVehiclesDataSource.get(position);
        DeleteData dd = new DeleteData(this, mVehicleData);
        dd.execute();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == 121) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.garage_deleteVehicleWarningTitle)
                    .setMessage(R.string.garage_deleteVehicleWarningMessage)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton){
                            onVehicleDelete(item.getGroupId());
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();

            return true;
        }
        return super.onContextItemSelected(item);
    }


    private void displayMessage(String message){
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }

}