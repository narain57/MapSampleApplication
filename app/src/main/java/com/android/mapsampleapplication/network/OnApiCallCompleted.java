package com.android.mapsampleapplication.network;

import com.android.mapsampleapplication.model.Vehicle;
import com.android.mapsampleapplication.model.VehicleList;

import java.util.ArrayList;

import retrofit2.Response;

public interface OnApiCallCompleted {

    void onSuccess(Response response);
    void onFailure(Throwable t);
}
