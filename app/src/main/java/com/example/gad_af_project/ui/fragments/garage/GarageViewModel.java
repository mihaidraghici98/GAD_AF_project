package com.example.gad_af_project.ui.fragments.garage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GarageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GarageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Garage fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}