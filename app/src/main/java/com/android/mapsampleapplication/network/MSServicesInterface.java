package com.android.mapsampleapplication.network;

import com.android.mapsampleapplication.model.VehicleList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MSServicesInterface {

    @GET
    Call<VehicleList> fetchVehicleDetails(@Url String url);
}
