package com.example.gad_af_project.ui.garage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gad_af_project.R;
import com.example.gad_af_project.vehicles.Vehicle;
import com.example.gad_af_project.vehicles.VehiclesAdapter;

import java.util.ArrayList;

public class GarageFragment extends Fragment {

    private VehiclesAdapter mAdapter;
    private RecyclerView mList;
    private ArrayList<Vehicle> mVehiclesDataSource = new ArrayList<Vehicle>(){{
        add(new Vehicle.Builder("IF 55 DGL").build());
        add(new Vehicle.Builder("B 123 ABC").build());
        add(new Vehicle.Builder("B 00 CRI").build());
    }};

    private GarageViewModel garageViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        garageViewModel =
                new ViewModelProvider(this).get(GarageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_garage, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);

        // ========== Recycler View
        mList = root.findViewById(R.id.fragmentGarage_vehicleList);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new VehiclesAdapter(mVehiclesDataSource);
        mList.setAdapter(mAdapter);

        // ============================


//        garageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}