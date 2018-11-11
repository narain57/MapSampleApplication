package com.android.mapsampleapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VehicleList {

    @SerializedName("poiList")
    private List<Vehicle> vehicleArrayList;

    public List<Vehicle> getVehicleArrayList() {
        return vehicleArrayList;
    }

    public void setVehicleArrayList(List<Vehicle> vehicleArrayList) {
        this.vehicleArrayList = vehicleArrayList;
    }
}
