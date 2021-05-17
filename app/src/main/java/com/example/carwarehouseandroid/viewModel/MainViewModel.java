package com.example.carwarehouseandroid.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.repository.CarRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    public static final String TAG = "MainViewModel->";

    private CarRepository carRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        //init car repo
        carRepository = new CarRepository(application.getApplicationContext());
    }

    // CAR REPOSITORY =================================================================
    public LiveData<List<Car>> getAllCars(){
        Log.d(TAG, "getAllCars: ");
        return carRepository.getAllCars();
    }
    // END CAR REPOSITORY =============================================================
}
