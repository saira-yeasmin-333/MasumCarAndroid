package com.example.carwarehouseandroid.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.api.JsonApiEndPoints;
import com.example.carwarehouseandroid.rootModel.CarRoot;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarRepository {
    public static final String TAG = "CarRepository->";

    private String token;
    private JsonApiEndPoints endpoints;

    //mutable live data to post value when car data is received
    private MutableLiveData<List<Car>> cars = new MutableLiveData<>();


    public CarRepository(Context context){
        endpoints = ApiService.getInstance().getJsonApiEndPoints();

        //get the token
        SharedPreferences sharedPreferences=context.getSharedPreferences(TokenManager.REFNAME,TokenManager.Mode);
        token=sharedPreferences.getString(TokenManager.KEY_JWT_TOKEN,"None");
    }

    public LiveData<List<Car>> getAllCars(){
        Log.d(TAG, "getAllCalls: call fetch cars and returning cars live data");
        this.fetchAllCars();
        return cars;
    }

    private void fetchAllCars() {
        if(token.equals("None")){
            Log.e(TAG, "getAllCars: token is None. Can't call api.");
            return;
        }

        Log.d(TAG, "fetchAllCars: calling api end points");

       endpoints.AllMCars("Bearer "+token).enqueue(new Callback<CarRoot>() {
            @Override
            public void onResponse(Call<CarRoot> call, Response<CarRoot> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "fetchAllCars():onResponse(): data received successfully");
                    CarRoot carRoot = response.body();
                    if(carRoot!=null)
                        cars.postValue(carRoot.getCars());
                    else
                        Log.e(TAG, "onResponse: CarRoot is null");
                }else{
                    Log.e(TAG, "onResponse: CarRoot call is unsuccessful. Response code: "+response.code()) ;
                }
            }

            @Override
            public void onFailure(Call<CarRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
