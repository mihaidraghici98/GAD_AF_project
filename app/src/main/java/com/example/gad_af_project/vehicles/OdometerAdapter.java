package com.example.gad_af_project.vehicles;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gad_af_project.MyApplication;
import com.example.gad_af_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OdometerAdapter extends RecyclerView.Adapter<OdometerAdapter.OdometerViewHolder>{

    private static final String LOG_TAG = "OdometerAdapter_TAG";
    // maps virtual position to type and actual index (<virtual_position, <type, actual_position>>
    private final HashMap<Integer, Pair<Integer, Integer>> mTypeMapping;
    private ArrayList<String> mHeadersString;
    private ArrayList<OdometerHistory> mOdometerEntries;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ODOMETER = 1;

    public OdometerAdapter(HashMap<String, ArrayList<OdometerHistory>> mDataBucket, SimpleDateFormat initialFormatter, SimpleDateFormat postFormatter){
        mTypeMapping = new HashMap<>();
        mHeadersString = new ArrayList<>();
        mOdometerEntries = new ArrayList<>();
        int map_index = 0;
        int header_index = 0;
        int odometer_index = 0;
        mHeadersString = new ArrayList<>(mDataBucket.keySet());
        Collections.sort(mHeadersString, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Date d1 = null;
                try {
                    d1 = initialFormatter.parse(o1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return -1;
                }

                Date d2 = null;
                try {
                    d2 = initialFormatter.parse(o2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 1;
                }
                return d1.compareTo(d2);
            }
        });

        for(String header : mHeadersString){
            this.mTypeMapping.put(map_index++, Pair.create(TYPE_HEADER, header_index++));
            ArrayList<OdometerHistory> mOdometerHistoryBucket = mDataBucket.get(header);
            Collections.sort(mOdometerHistoryBucket, new Comparator<OdometerHistory>() {
                @Override
                public int compare(OdometerHistory o1, OdometerHistory o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            for(OdometerHistory oH : mOdometerHistoryBucket){
                mTypeMapping.put(map_index++, Pair.create(TYPE_ODOMETER, odometer_index++));
                mOdometerEntries.add(oH);
            }
        }
        // transform all headers to the desired postProcessing format
        ArrayList<String> aux = new ArrayList<>();
        for(String el : mHeadersString){
            Date d = null;
            try {
                d = initialFormatter.parse(el);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            aux.add(postFormatter.format(d).toString().toLowerCase());
        }
        mHeadersString = aux;
    }

    @NonNull
    @Override
    public OdometerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_odometer, parent, false);
            return new EntryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OdometerViewHolder holder, int position) {
        int type = mTypeMapping.get(position).first;

        if(type == TYPE_HEADER){
            DataWrapper dW = new DataWrapper(mHeadersString.get(mTypeMapping.get(position).second), null);
            holder.update(dW);
        }
        if(type == TYPE_ODOMETER){
            DataWrapper dW = new DataWrapper(null, mOdometerEntries.get(mTypeMapping.get(position).second));
            holder.update(dW);
        }
    }

    @Override
    public int getItemCount() {
        return mTypeMapping.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTypeMapping.get(position).first;
    }

    private class DataWrapper{
        private String headerString;
        private OdometerHistory odometerHistory;
        public DataWrapper(String headerString, OdometerHistory odometerHistory){
            this.headerString = headerString;
            this.odometerHistory = odometerHistory;
        }
        public OdometerHistory getOdometerHistory() { return odometerHistory; }
        public String getHeaderString() { return headerString; }
    }

    public abstract static class OdometerViewHolder extends RecyclerView.ViewHolder
    {
        public OdometerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void update(DataWrapper value) {};
    }

    public class EntryViewHolder extends OdometerAdapter.OdometerViewHolder implements View.OnCreateContextMenuListener
    {
        private LinearLayout layout;
        private final TextView mTV_Date;
        private final TextView mTV_Value;
        private final TextView mTV_Unit;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);

            mTV_Date = itemView.findViewById(R.id.tv_odometerHistory_item_date);
            mTV_Value = itemView.findViewById(R.id.tv_odometerHistory_item_value);
            mTV_Unit = itemView.findViewById(R.id.tv_odometerHistory_item_unit);
            layout = itemView.findViewById(R.id.layout_odometerHistory_item);
            layout.setOnCreateContextMenuListener(this);
        }

        public void update(DataWrapper value) {
            OdometerHistory oH = value.getOdometerHistory();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            mTV_Date.setText(formatter.format(oH.getDate()));
            mTV_Value.setText(Integer.toString(oH.getValue()));
            mTV_Unit.setText(oH.getUnit());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int adapterPos = this.getAdapterPosition();
            int entryIndex = mTypeMapping.get(adapterPos).second;
            int entryId = mOdometerEntries.get(entryIndex).getEntryId();
            Log.v(LOG_TAG, "adapterPos:" + adapterPos + " entryIndex: " + entryIndex +  " entryId: " + entryId);
            menu.add(entryId, 221, 0, MyApplication.getR().getString(R.string.odometerHistory_contextmenu_delete));
        }
    }

    public static class HeaderViewHolder extends OdometerAdapter.OdometerViewHolder
    {
        private final TextView mTV_Title;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            mTV_Title = itemView.findViewById(R.id.tv_odometerHistory_header);
        }

        public void update(DataWrapper value) {
            mTV_Title.setText(value.getHeaderString());
        }
    }

}
