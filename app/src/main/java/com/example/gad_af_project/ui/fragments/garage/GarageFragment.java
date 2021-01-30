package com.example.gad_af_project.ui.fragments.garage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gad_af_project.MyApplication;
import com.example.gad_af_project.NewVehicleActivity;
import com.example.gad_af_project.R;
import com.example.gad_af_project.database.AppDatabase;
import com.example.gad_af_project.vehicles.Vehicle;
import com.example.gad_af_project.VehicleActivity;
import com.example.gad_af_project.vehicles.VehiclesAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GarageFragment extends Fragment implements VehiclesAdapter.VehiclesViewHolder.OnVehicleListener {

    private VehiclesAdapter mAdapter;
    private RecyclerView mList;
    private List<Vehicle> mVehiclesDataSource;

    private Button mAddButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_garage, container, false);
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

    private void getData() {
        class GetData extends AsyncTask<Void, Void, List<Vehicle>>{
            @Override
            protected List<Vehicle> doInBackground(Void... voids) {
                return AppDatabase.getAppDatabase(null).vehicleDao().getAllVehicles();
            }

            @Override
            protected void onPostExecute(List<Vehicle> vehicles) {
                mVehiclesDataSource = vehicles;
                mAdapter = new VehiclesAdapter(mVehiclesDataSource, GarageFragment.this);
                mList.setAdapter(mAdapter);
                super.onPostExecute(vehicles);
            }
        }
        GetData gd = new GetData();
        gd.execute();
    }

    @Override
    public void onVehicleClick(int position) {
        Vehicle mVehicleData = mVehiclesDataSource.get(position);
        Intent intent = VehicleActivity.getStartingIntent(getContext(), mVehicleData.getVehicleId());
        startActivity(intent);
    }
}