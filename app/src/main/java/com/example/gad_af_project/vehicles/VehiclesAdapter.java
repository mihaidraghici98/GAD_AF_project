package com.example.gad_af_project.vehicles;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gad_af_project.R;

import java.util.ArrayList;

public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.VehiclesViewHolder>{


    private final ArrayList<Vehicle> mDataSet;

    public VehiclesAdapter(ArrayList<Vehicle> vehicles_list){
        mDataSet = vehicles_list;
    }

    @NonNull
    @Override
    public VehiclesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_vehicle, parent, false);
        return new VehiclesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiclesViewHolder holder, int position) {
        holder.update(mDataSet.get(position).getPlateNo());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class VehiclesViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mVeh_PlateNo;

        public VehiclesViewHolder(@NonNull View itemView) {
            super(itemView);
            mVeh_PlateNo = itemView.findViewById(R.id.listVehiclesEntry_plateNo);
        }

        public void update(String value) {
            mVeh_PlateNo.setText(value);
        };
    }

}
