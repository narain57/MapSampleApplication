package com.android.mapsampleapplication.network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitCallback<T> implements Callback<T> {
    private final MSRetrofitCallBackInterface<T> listener;

    public RetrofitCallback(MSRetrofitCallBackInterface<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.body() != null && response.isSuccessful()) {
            listener.onSuccess(response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        listener.onFailure(t);
    }
}
