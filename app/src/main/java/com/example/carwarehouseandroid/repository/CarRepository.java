package com.example.carwarehouseandroid.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.carwarehouseandroid.Model.AuthToken;
import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.Model.CarImage;
import com.example.carwarehouseandroid.Model.UserLogin;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.api.JsonApiEndPoints;
import com.example.carwarehouseandroid.room.CarRepositoryRoom;
import com.example.carwarehouseandroid.rootModel.CarRegister;
import com.example.carwarehouseandroid.rootModel.CarRoot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarRepository {
    public static final String TAG = "CarRepository->";

    private String token;
    private JsonApiEndPoints endpoints;
    private TokenManager tokenManager;

    //mutable live data to post value when car data is received
    private MutableLiveData<List<Car>> cars = new MutableLiveData<>();
    private MutableLiveData<Integer>isAdded=new MutableLiveData<>();
    private MutableLiveData<Integer>isUpdated=new MutableLiveData<>();
    private MutableLiveData<Integer>isDeleted=new MutableLiveData<>();
    private MutableLiveData<Integer>isBought=new MutableLiveData<>();
    public CarRepository(Context context){
        endpoints = ApiService.getInstance().getJsonApiEndPoints();
        tokenManager=new TokenManager(context);
        //get the token
        SharedPreferences sharedPreferences=context.getSharedPreferences(TokenManager.REFNAME,TokenManager.Mode);
        token=sharedPreferences.getString(TokenManager.KEY_JWT_TOKEN,"None");
    }

    public LiveData<List<Car>> getAllCars(){
        Log.d(TAG, "getAllCalls: call fetch cars and returning cars live data");
        this.fetchAllCars();
        return cars;
    }

    public LiveData<List<Car>> getViewerCars(){
        Log.d(TAG, "getAllCalls: call fetch cars and returning cars live data");
        this.fetchAllViewerCar();
        return cars;
    }

    public void fetchAllCars() {
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

                    if(carRoot!=null) {
                        cars.setValue(null);
                        cars.postValue(carRoot.getCars());

                    }
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

    public void fetchAllViewerCar() {

        Log.d(TAG, "fetchAllCars: calling api end points");

        endpoints.AllCar1().enqueue(new Callback<CarRoot>() {
            @Override
            public void onResponse(Call<CarRoot> call, Response<CarRoot> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "fetchAllCars():onResponse(): data received successfully");
                    CarRoot carRoot = response.body();
                    if(carRoot!=null){
                        cars.setValue(null);
                        cars.postValue(carRoot.getCars());

                    }

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

    public interface OnTokenReceivedListener{
        void onReceived(String token);
    }

    private OnTokenReceivedListener onTokenReceivedListener;

    public void setOnTokenReceivedListener(OnTokenReceivedListener onTokenReceivedListener) {
        this.onTokenReceivedListener = onTokenReceivedListener;
    }

    public void loginApiCall(UserLogin obj){
        endpoints.login(obj).enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if(response.isSuccessful()){
                    AuthToken authToken=response.body();
                    tokenManager.createSession(authToken.getUsername(),authToken.getAccess_token(),authToken.getUserId());
                    if(onTokenReceivedListener!=null)
                        onTokenReceivedListener.onReceived(authToken.getAccess_token());
                }
                else{
                    Log.e(TAG, "onResponse: loginApi call is unsuccessful. Response code: "+response.code()) ;
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    public LiveData<Integer> isUpdated(Car car){
        this.updateCar(car);
        return isUpdated;
    }
    private void updateCar(Car car){
        isUpdated=new MutableLiveData<>();
        endpoints.updateCar(car,"Bearer "+token).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    isUpdated.postValue(response.body().getIsSuccess());
                }
                else{
                    Log.e(TAG, "onResponse: CarRoot call is unsuccessful. Response code: "+response.code()) ;
                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void addCar(Car car){
        isAdded=new MutableLiveData<>();
        endpoints.addNewCar(car,"Bearer "+token).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    int i=response.body().getIsSuccess();
                    isAdded.postValue(i);
                    Log.e(TAG, "onResponse: "+isAdded );
                }
                else{
                    Log.e(TAG, "onResponse: CarRoot call is unsuccessful. Response code: "+response.code()) ;
                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {
               t.printStackTrace();
            }
        });
    }


    private void deleteCar(Car car){
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("id", car.getRegistration_id());
        parameters.put("userId", car.getM_id());
        isDeleted=new MutableLiveData<>();
        endpoints.deleteCar(parameters,"Bearer "+token).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    isDeleted.postValue(response.body().getIsSuccess());
                }else{
                    Log.e(TAG, "onResponse: CarRoot call is unsuccessful. Response code: "+response.code()) ;

                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {
               t.printStackTrace();
            }
        });
    }

    public LiveData<Integer>  isDeleted(Car car){
        this.deleteCar(car);
        return isDeleted;
    }

    public LiveData<Integer>  isAdded(Car car){
       this.addCar(car);
       return isAdded;
    }
    public LiveData<Integer>  isBought(Car car){
        this.buyCar(car);
        return isBought;
    }
    private void buyCar(Car car){
        isBought=new MutableLiveData<>();
        endpoints.buyCar(car.getRegistration_id()).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    isBought.postValue(response.body().getIsSuccess());
                }else{
                    Log.e(TAG, "onResponse: CarRoot call is unsuccessful. Response code: "+response.code()) ;

                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
