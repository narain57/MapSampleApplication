package com.android.mapsampleapplication.view;

import com.android.mapsampleapplication.model.Vehicle;

import java.util.ArrayList;

public interface MainView {

    void showProgress();
    void hidProgress();
    void setVehicleList(ArrayList<Vehicle> body);
    void onFailure(Throwable t);
}
