package com.example.gad_af_project.ui.fuel;

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

import com.example.gad_af_project.R;

public class FuelFragment extends Fragment {

    private FuelViewModel fuelViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fuelViewModel =
                new ViewModelProvider(this).get(FuelViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fuel, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        fuelViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}