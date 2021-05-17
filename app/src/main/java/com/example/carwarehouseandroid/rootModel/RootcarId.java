package com.example.carwarehouseandroid.rootModel;

import com.example.carwarehouseandroid.Model.Car;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RootcarId {

    @SerializedName("Car")
    @Expose
    private Car car;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
