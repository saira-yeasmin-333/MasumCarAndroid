package com.example.carwarehouseandroid.rootModel;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.carwarehouseandroid.Model.Car;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarRoot {

    @SerializedName("results")
    @Expose
    private List<Car>cars = null;

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}
