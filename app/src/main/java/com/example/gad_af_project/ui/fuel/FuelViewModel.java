package com.example.gad_af_project.ui.fuel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FuelViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FuelViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Fuel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}