package com.android.mapsampleapplication.network;

import com.android.mapsampleapplication.model.VehicleList;

import retrofit2.Call;
import retrofit2.Response;

public class VehicleInteractorImpl implements VehicleInteractor {

    private String lat1 = "53.694865";
    private String lat2 = "53.394655";

    private String long1 = "9.757589";
    private String long2 = "10.099891";

    @Override
    public void fetchVehicleList(final OnApiCallCompleted listener) {
        final Call<VehicleList> vehicleDetailsCall = NetworkHandler.getClientInstance().fetchVehicleDetails("https://fake-poi-api.mytaxi.com/?p1Lat="+lat1+"&p1Lon="+long1+"&p2Lat="+lat2+"&p2Lon="+long2);

        RetrofitCallback callback = new RetrofitCallback(new MSRetrofitCallBackInterface<VehicleList>() {
            @Override
            public void onSuccess(Response<VehicleList> response) {
                listener.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onFailure(t);
            }
        });
        vehicleDetailsCall.enqueue(callback);

    }
}
