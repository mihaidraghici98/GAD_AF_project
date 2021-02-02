package com.example.gad_af_project.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.gad_af_project.R;
import com.example.gad_af_project.database.AppDatabase;
import com.example.gad_af_project.vehicles.OdometerHistory;
import com.example.gad_af_project.vehicles.Vehicle;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OdometerHistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private final String LOG_TAG = "OdometerHistoryAct_TAG";
    public final static String IKEY_VEHICLE_ID = "com.example.intent.keys.VEHICLE_ID";

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mRefreshButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(), "Nimic selectat!", Toast.LENGTH_LONG);
    }

    @Override
    public void onClick(View v) {
        if(v == mRefreshButton){
            refreshChart();
            mRefreshButton.setVisibility(View.INVISIBLE);
        }
    }

    enum Granularity {
        Day,
        Week,
        Month,
        Year
    }

    private int mVehicleId;
    private Vehicle mVehicleData;
    private List<OdometerHistory> mOdometerHistory;

    private Button mRefreshButton;
    private TextView mPlateNumber;
    private Spinner mSpinnerGranularity;
    private Spinner mSpinnerChartType;
    private AnyChartView mLineChart;
    private AnyChartView mColumnChart;

    private Cartesian mLineCartesian;
    private Cartesian mColumnCartesian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odometer_history);

        Intent intent = getIntent();
        if(intent != null){
            mVehicleId = intent.getIntExtra(IKEY_VEHICLE_ID, -1);
        }
        else {
            mVehicleId = -1;
        }

        mPlateNumber = findViewById(R.id.tv_odometerHistory_plateNumber);

        mColumnChart = findViewById(R.id.columnChart_odometer);
        APIlib.getInstance().setActiveAnyChartView(mColumnChart);
        mColumnCartesian = AnyChart.column();
        mColumnChart.setChart(mColumnCartesian);
        mColumnChart.setVisibility(View.GONE);
        mLineChart = findViewById(R.id.lineChart_odometer);
        APIlib.getInstance().setActiveAnyChartView(mLineChart);
        mLineCartesian = AnyChart.line();
        mLineChart.setChart(mLineCartesian);

        mSpinnerChartType = findViewById(R.id.spinner_odometerHistory_type);
        String[] chartType = new String[]{
                getResources().getString(R.string.chartType_cumulative),
                getResources().getString(R.string.chartType_growth)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, chartType);
        mSpinnerChartType.setAdapter(adapter);
        mSpinnerChartType.setOnItemSelectedListener(this);

        mSpinnerGranularity = findViewById(R.id.spinner_odometerHistory_granularity);
        String[] granularity = new String[]{
                getResources().getString(R.string.day),
                getResources().getString(R.string.week),
                getResources().getString(R.string.month),
                getResources().getString(R.string.year)
        };
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, granularity);
        mSpinnerGranularity.setAdapter(adapter);
        mSpinnerGranularity.setOnItemSelectedListener(this);

        mRefreshButton = findViewById(R.id.bt_odometerHistory_confirmGranularity);
        mRefreshButton.setOnClickListener(this);
        mRefreshButton.setVisibility(View.INVISIBLE);


        getData();
    }

    @NotNull
    public static Intent getStartingIntent(Context ctx, int vehicle_id) {
        Intent intent = new Intent(ctx, OdometerHistoryActivity.class);
        intent.putExtra(IKEY_VEHICLE_ID, vehicle_id);
        return intent;
    }

    private int maxOdometerValue(ArrayList<OdometerHistory> list){
        if(list.size() < 1)
            return -1;
        int max = list.get(0).getValue();
        for(OdometerHistory oh : list){
            if(max < oh.getValue())
                max = oh.getValue();
        }
        return max;
    }

    private void refreshChart(){
        // type == "cumulative" - line chart / "growth" - bar chart
        if(mOdometerHistory == null)
            return;

        Log.v(LOG_TAG, "refreshChart initiated!");
        String granularityString = mSpinnerGranularity.getSelectedItem().toString();
        Granularity granularity = getGranularityEnumFromString(granularityString);
        String type = mSpinnerChartType.getSelectedItem().toString();
        Log.v(LOG_TAG, "granularityString: " + granularityString + " : " + granularity.toString());
        SimpleDateFormat initialFormatter;
        SimpleDateFormat postFormatter;
        HashMap<String, ArrayList<OdometerHistory>> bucket = new HashMap<>();
        if(granularity == Granularity.Day) {
            initialFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            postFormatter = new SimpleDateFormat("dd-MMM", Locale.US);
        }
        else if(granularity == Granularity.Week) {
            initialFormatter = new SimpleDateFormat("w-yyyy", Locale.US);
            postFormatter = new SimpleDateFormat("dd-MMM", Locale.US);
        }
        else if(granularity == Granularity.Month) {
            initialFormatter = new SimpleDateFormat("MMM-yyyy", Locale.US);
            postFormatter = new SimpleDateFormat("MMM", Locale.US);
        }
        else if(granularity == Granularity.Year){
            initialFormatter = new SimpleDateFormat("yyyy", Locale.US);
            postFormatter = new SimpleDateFormat("yyyy", Locale.US);
        }else
            return;


        for(OdometerHistory oH : mOdometerHistory) {
            Log.v(LOG_TAG, "Here is an entry of OdometerHistory: " + oH.getValue() + " on " + oH.getDate().toString());
            String date_string = initialFormatter.format(oH.getDate());
            if (!bucket.containsKey(date_string))
                bucket.put(date_string, new ArrayList<OdometerHistory>());
            bucket.get(date_string).add(oH);
        }
        Log.v(LOG_TAG, "Bucket: " + bucket.toString());

        ArrayList<Pair<Date, Integer>> aggregatedList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<OdometerHistory>> pair : bucket.entrySet()) {
            Date key = null;
            try {
                key = initialFormatter.parse(pair.getKey());
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG);
            }
            int value = maxOdometerValue(pair.getValue());
            aggregatedList.add(new Pair<>(key, value));
        }

        Log.v(LOG_TAG, "aggregatedList: " + aggregatedList.toString());

        Collections.sort(aggregatedList, new Comparator<Pair<Date, Integer>>() {
            @Override
            public int compare(Pair<Date, Integer> o1, Pair<Date, Integer> o2) {
                return o1.first.compareTo(o2.first);
            }
        });


        //todo -> parcurge Hashmap si adauga in ArrayList pair<String, aggragetInt>
        //todo -> asigura ca ordinea e crescatoare

        //todo -> treci mai departe cu generarea chart-ului in functie de Type

        // TODO agregare valori zilnice/saptamanale/lunare/anuale cu buckets
        // TODO in RecyclerView sa fie grupate dupa granularitatea selectata

        List<DataEntry> data = new ArrayList<>();

        if(type.equals(getResources().getString(R.string.chartType_cumulative))){
            mLineChart.setVisibility(View.VISIBLE);
            mColumnChart.setVisibility(View.GONE);
            APIlib.getInstance().setActiveAnyChartView(mLineChart);

            Calendar cal = Calendar.getInstance();
            Date lastDate = null;
            for(Pair<Date, Integer> pair : aggregatedList){
                if(lastDate != null){
                    long diffInMillis = Math.abs(pair.first.getTime() - lastDate.getTime());
                    if(granularity == Granularity.Day){
                        long daysBetween = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) - 1;
                        cal.setTime(lastDate);
                        for(int i = 0; i < daysBetween; i++){
                            cal.add(Calendar.DATE, 1);
                            data.add(new ValueDataEntry(postFormatter.format(cal.getTime()), null));
                        }
                    }
                    else if(granularity == Granularity.Week){
                        long weeksBetween = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)/7 - 1;
                        cal.setTime(lastDate);
                        for(int i = 0; i < weeksBetween; i++){
                            cal.add(Calendar.DATE, 7);
                            data.add(new ValueDataEntry(postFormatter.format(cal.getTime()), null));
                        }
                    }
                    else if (granularity == Granularity.Month){
                        long monthsBetween = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)/31 - 1;
                        cal.setTime(lastDate);
                        for(int i = 0; i < monthsBetween; i++){
                            cal.add(Calendar.DATE, 31);
                            data.add(new ValueDataEntry(postFormatter.format(cal.getTime()), null));
                        }
                    }
                    else if(granularity == Granularity.Year){
                        long yearsBetween = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)/365 - 1;
                        cal.setTime(lastDate);
                        for(int i = 0; i < yearsBetween; i++){
                            cal.add(Calendar.DATE, 365);
                            data.add(new ValueDataEntry(postFormatter.format(cal.getTime()), null));
                        }
                    }
                }
                data.add(new ValueDataEntry(postFormatter.format(pair.first).toLowerCase(), pair.second));
                lastDate = pair.first;
            }

            mLineCartesian.removeAllSeries();
            Line series1 = mLineCartesian.line(data);
            series1.name(mVehicleData.getOdometerUnit());
            series1.hovered().markers().enabled(true);
            series1.hovered().markers().type(MarkerType.CIRCLE).size(4d);
            series1.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5d).offsetY(5d);

            series1.connectMissingPoints(true);
        }
        else {
            mLineChart.setVisibility(View.GONE);
            mColumnChart.setVisibility(View.VISIBLE);
            APIlib.getInstance().setActiveAnyChartView(mColumnChart);

            Log.v(LOG_TAG, "Crestere");
            int lastValue = 0;
            for(Pair<Date, Integer> pair : aggregatedList){
                data.add(new ValueDataEntry(postFormatter.format(pair.first).toLowerCase(), pair.second - lastValue));
                lastValue = pair.second;
            }
            mColumnCartesian.removeAllSeries();
            Column column = mColumnCartesian.column(data);

            column.name(mVehicleData.getOdometerUnit());

            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(5d);

            mColumnCartesian.animation(true);

            mColumnCartesian.yScale().minimum(0d);

            mColumnCartesian.tooltip().positionMode(TooltipPositionMode.POINT);
            mColumnCartesian.interactivity().hoverMode(HoverMode.BY_X);

            mColumnCartesian.yAxis(0).title(mVehicleData.getOdometerUnit());

        }

    }

    public int getVehicleId(){
        return mVehicleId;
    }
    public void setOdometerHistory(List<OdometerHistory> odometerHistoryList){
        this.mOdometerHistory = odometerHistoryList;
    }
    public void setVehicleData(Vehicle vehicle){
        this.mVehicleData = vehicle;
    }
    public void setTextOnPlateNumber(String text){
        mPlateNumber.setText(text);
    }

    public Granularity getGranularityEnumFromString(String g){
        if(g.equals(getResources().getString(R.string.day)))
            return Granularity.Day;
        if(g.equals(getResources().getString(R.string.week)))
            return Granularity.Week;
        if(g.equals(getResources().getString(R.string.month)))
            return Granularity.Month;
        if(g.equals(getResources().getString(R.string.year)))
            return Granularity.Year;
        return null;
    }


    static class GetData extends AsyncTask<Void, Void, Vehicle> {
        WeakReference<OdometerHistoryActivity> mWeakActivity;

        public GetData(OdometerHistoryActivity activity){
            this.mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        protected Vehicle doInBackground(Void... voids) {
            int mVehicleId = mWeakActivity.get().getVehicleId();
            if(mVehicleId == -1)
                return null;

            mWeakActivity.get().setOdometerHistory(AppDatabase.getAppDatabase(null).odometerDao().getOdometerForVehicle(mVehicleId));
            return AppDatabase.getAppDatabase(mWeakActivity.get().getApplicationContext()).vehicleDao().getVehicleById(mVehicleId);
        }

        @Override
        protected void onPostExecute(Vehicle vehicle) {
            super.onPostExecute(vehicle);
            if(vehicle == null)
                return;
            mWeakActivity.get().setVehicleData(vehicle);

            Toast.makeText(mWeakActivity.get().getApplicationContext(), "I will populate chart", Toast.LENGTH_LONG).show();

            mWeakActivity.get().setTextOnPlateNumber(vehicle.getPlateNumberFormatted());
            mWeakActivity.get().refreshChart();
        }
    }
    private void getData(){
        GetData gd = new GetData(this);
        gd.execute();
    }
}
