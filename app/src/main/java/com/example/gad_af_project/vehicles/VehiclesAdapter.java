package com.example.gad_af_project.vehicles;

import android.text.Layout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gad_af_project.MyApplication;
import com.example.gad_af_project.R;

import java.util.List;

public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.VehiclesViewHolder>{

    private final List<Vehicle> mDataSet;
    private VehiclesViewHolder.OnVehicleListener mOnVehicleListener;

    public VehiclesAdapter(List<Vehicle> vehicles_list, VehiclesViewHolder.OnVehicleListener onVehicleListener){
        mDataSet = vehicles_list;
        this.mOnVehicleListener = onVehicleListener;
    }

    @NonNull
    @Override
    public VehiclesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_vehicle, parent, false);
        return new VehiclesViewHolder(view, mOnVehicleListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiclesViewHolder holder, int position) {
        holder.update(mDataSet.get(position).getPlateNumberFormatted());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class VehiclesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener
    {
        private final TextView mVeh_PlateNo;
        private RelativeLayout relativeLayout;
        OnVehicleListener onVehicleListener;

        public VehiclesViewHolder(@NonNull View itemView, OnVehicleListener onVehicleListener) {
            super(itemView);
            mVeh_PlateNo = itemView.findViewById(R.id.listVehiclesEntry_plateNo);
            this.onVehicleListener = onVehicleListener;

            relativeLayout = itemView.findViewById(R.id.garage_vehicle_item);
            relativeLayout.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(this);
        }

        public void update(String value) {
            mVeh_PlateNo.setText(value);
        };

        @Override
        public void onClick(View v) {
            onVehicleListener.onVehicleClick(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 121, 0, MyApplication.getR().getString(R.string.garage_contextmenu_delete));
        }

        public interface OnVehicleListener{
            void onVehicleClick(int position);
            void onVehicleDelete(int position);
        }
    }



}
