package com.android.mapsampleapplication.presenter;

import com.android.mapsampleapplication.model.Vehicle;
import com.android.mapsampleapplication.model.VehicleList;
import com.android.mapsampleapplication.network.OnApiCallCompleted;
import com.android.mapsampleapplication.network.VehicleInteractor;
import com.android.mapsampleapplication.view.MainView;

import java.util.ArrayList;

import retrofit2.Response;

public class VehiclePresenterImpl implements VehiclePresenter,OnApiCallCompleted {


    private MainView view;
    private VehicleInteractor interactor;

    public VehiclePresenterImpl(MainView view, VehicleInteractor interactor) {
        this.interactor = interactor;
        this.view = view;
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void fetchVehicleDetails() {
        view.showProgress();
        interactor.fetchVehicleList(this);
    }

    @Override
    public void onSuccess(Response response) {
        if(view !=null) {
            view.hidProgress();
            view.setVehicleList((ArrayList<Vehicle>)((VehicleList)response.body()).getVehicleArrayList());
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if(view !=null) {
            view.hidProgress();
            view.onFailure(t);
        }
    }
}

