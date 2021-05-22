package com.example.carwarehouseandroid.viewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.Model.CarImage;
import com.example.carwarehouseandroid.Model.UserLogin;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.JsonApiEndPoints;
import com.example.carwarehouseandroid.repository.CarRepo;
import com.example.carwarehouseandroid.repository.CarRepository;
import com.example.carwarehouseandroid.room.CarRepositoryRoom;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    public static final String TAG = "MainViewModel->";
    private String token;

    private CarRepo carRepo;
    private CarRepository carRepository;
    private CarRepositoryRoom repository;
    private LiveData<List<Car>> allCars;
    private Application application;

    public MainViewModel(@NonNull Application application) {
        super(application);
        //get the token
       /* TokenManager tokenManager = new TokenManager(application);
        SharedPreferences sharedPreferences = application.getSharedPreferences(TokenManager.REFNAME, TokenManager.Mode);
        token = sharedPreferences.getString(TokenManager.KEY_JWT_TOKEN, "None");*/

        //init car repo
        carRepo = new CarRepo(application,token);
        this.application=application;
    }

    public void setToken(String token) {
        this.token = token;
        carRepo=new CarRepo(application.getApplicationContext(),token);
    }

    // CAR REPOSITORY =================================================================


    public void loginUser(UserLogin obj, CarRepo.OnLoginSuccessListener listener) {
        carRepo.loginApiCall(obj);
        carRepo.setOnLoginSuccessListener(listener);
    }
    public LiveData<List<Car>> getAllCars() {
        Log.d(TAG, "getAllCars: ");
        return carRepo.getAllCars();
    }

    public void fetchCarList(){
        Log.d(TAG, "fetchCarList(): ");
        carRepo.fetchCars();
    }


    public LiveData<List<Car>> getAllViewerCars() {
        Log.d(TAG, "getAllCars: ");
        return carRepo.getAllViewerCars();
    }

    public void fetchCarViewerList(){
        Log.d(TAG, "fetchCarList(): ");
        carRepo.fetchAllViewerCars();
    }

    public LiveData<Boolean> updateCar(Car car){
        Log.d(TAG, "updateCar(): ");
        return carRepo.updateCar(car);
        //Log.d(TAG, "updateCar(): "+car);
    }

    public LiveData<Boolean> addCar(Car car) {
        LiveData<Boolean> i = carRepo.addCar(car);
        Log.e(TAG, "addCar: " + i);
        return i;
    }

    public LiveData<Boolean> deleteCar(Car car) {
        return carRepo.deleteCar(car);
    }

    public LiveData<Integer> buyCar(Car car) {
        LiveData<Integer> i = carRepo.buyCar(car);
        Log.e(TAG, "buyCar: in mvm " + i);
        return i;
    }

    public void deleteAllCars(){
        carRepo.deleteAllCars();
    }



    // END CAR REPOSITORY =============================================================
}
