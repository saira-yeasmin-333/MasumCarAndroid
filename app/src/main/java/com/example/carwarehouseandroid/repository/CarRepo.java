package com.example.carwarehouseandroid.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.carwarehouseandroid.Model.AuthToken;
import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.Model.UserLogin;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.api.JsonApiEndPoints;
import com.example.carwarehouseandroid.room.CarDao;
import com.example.carwarehouseandroid.room.CarDatabase;
import com.example.carwarehouseandroid.rootModel.CarRegister;
import com.example.carwarehouseandroid.rootModel.CarRoot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarRepo {
    public static final String TAG = "CarRepo";


    //room
    private CarDao carDao;

    //api call
    private String token;
    private JsonApiEndPoints endPoints;
    private TokenManager tokenManager;
    private OnLoginSuccessListener listener;

    public CarRepo(Context context,String token){
        //init car dao
        carDao = CarDatabase.getInstance(context).carDao();
        tokenManager=new TokenManager(context);
        //init api end point
        this.token = token;
        endPoints = ApiService.getInstance().getJsonApiEndPoints();
    }

    private boolean isCarListFetched = false;
    private boolean isCarViewerListFetched = false;
    public interface OnLoginSuccessListener{
        void loginSuccess(String token);
    }
    
    public void setOnLoginSuccessListener(OnLoginSuccessListener listener){
        this.listener=listener;
    }

    public void loginApiCall(UserLogin obj){
        endPoints.login(obj).enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if(response.isSuccessful()){
                    AuthToken authToken=response.body();
                    tokenManager.createSession(authToken.getUsername(),authToken.getAccess_token(),authToken.getUserId());
                    token=authToken.getAccess_token();
                    if(listener!=null){
                        listener.loginSuccess(token);
                    }
                    
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

    public LiveData<List<Car>> getAllCars(){
        if(!isCarListFetched){
            Log.d(TAG, "getAllCars(): car list not fetched yet");
            fetchCars();
            isCarListFetched = true;
        }else{
            Log.d(TAG, "getAllCars(): car list already fetched");
        }
        return carDao.getALLCars();
    }

    public LiveData<List<Car>> getAllViewerCars(){
        if(!isCarViewerListFetched){
            Log.d(TAG, "getAllViewerCars(): car list not fetched yet");
            fetchAllViewerCars();
            isCarViewerListFetched= true;
        }else{
            Log.d(TAG, "getAllViewerCars(): car list already fetched");
        }
        return carDao.getALLCars();
    }

    public LiveData<Boolean> updateCar(Car car){
        Log.e(TAG, "updateCar: token: "+token+"real token: ");
        MutableLiveData<Boolean> isUpdated = new MutableLiveData<>();
        endPoints.updateCar(car,"Bearer "+token).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "updateCar():onResponse: success");
                    int i=response.body().getIsSuccess();
                    if(i==1){
                        CarDatabase.executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run:"+car.toString());
                                carDao.update(car);
                                isUpdated.postValue(true);
                                Log.d(TAG, "updateCar(): data update in room");
                            }
                        });
                    }
                    else{
                        Log.d(TAG,"updateCar():onResponse(): car is nor updated.");

                    }
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
        return isUpdated;
    }


    public LiveData<Boolean> deleteCar(Car car){
       MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
       Map<String, Integer> parameters = new HashMap<>();
       parameters.put("id", car.getRegistration_id());
       parameters.put("userId", car.getM_id());
       endPoints.deleteCar(parameters,"Bearer "+token).enqueue(new Callback<CarRegister>() {
           @Override
           public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
               if(response.isSuccessful()){
                   int i=response.body().getIsSuccess();
                   if(i==1){
                       CarDatabase.executorService.execute(() -> {
                           carDao.delete(car);
                           Log.d(TAG, "run:"+car.toString());
                           isDeleted.postValue(true); 
                       });
                   }
                   
               }else{
                   Log.e(TAG, "onResponse: CarRoot call is unsuccessful. Response code: "+response.code()) ;

               }
           }

           @Override
           public void onFailure(Call<CarRegister> call, Throwable t) {
               t.printStackTrace();
           }
       });
       return isDeleted;
   }
   
    public LiveData<Boolean> addCar(Car car){
       Log.e(TAG, "addCar: token: "+token+"real token: ");
       MutableLiveData<Boolean> isAdded = new MutableLiveData<>();
       endPoints.addNewCar(car,"Bearer "+token).enqueue(new Callback<CarRegister>() {
           @Override
           public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
               if(response.isSuccessful()){
                   int i=response.body().getIsSuccess();
                   if(i==1){
                       CarDatabase.executorService.execute(() -> {
                           carDao.insert(car);
                           Log.d(TAG, "run:"+car.toString());
                           isAdded.postValue(true);
                           Log.e(TAG, "onResponse: "+isAdded );
                       });
                   }
                   
                   
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
       return isAdded;
   }

    public void fetchCars() {
        Log.d(TAG, "fetchCars: ");
        endPoints.AllMCars("Bearer "+token).enqueue(new Callback<CarRoot>() {
            @Override
            public void onResponse(Call<CarRoot> call, Response<CarRoot> response) {
                if(response.isSuccessful()){
                    List<Car> cars = response.body().getCars();
                    if(cars == null || cars.size() == 0 ){
                        Log.d(TAG,"fetchCars():onResponse(): no car data received.");
                        return;
                    }

                    CarDatabase.executorService.execute(() -> {
                        carDao.deleteAllCars();
                        carDao.insert(cars);
                        Log.d(TAG, "onResponse(): total "+cars.size()+" cars inserted successfully in car_table");
                    });

                }else{
                    Log.d(TAG, "onResponse(): response code:"+response.code());
                }

            }

            @Override
            public void onFailure(Call<CarRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void fetchAllViewerCars() {

        Log.d(TAG, "fetchAllViewerCars: calling api end points");

        endPoints.AllCar1().enqueue(new Callback<CarRoot>() {
            @Override
            public void onResponse(Call<CarRoot> call, Response<CarRoot> response) {
                if(response.isSuccessful()){
                    List<Car> cars = response.body().getCars();
                    if(cars == null || cars.size() == 0 ){
                        Log.d(TAG,"fetchCars():onResponse(): no car data received.");
                        return;
                    }

                    CarDatabase.executorService.execute(() -> {
                        carDao.deleteAllCars();
                        carDao.insert(cars);
                        Log.d(TAG, "onResponse(): total "+cars.size()+" cars inserted successfully in car_table");
                    });
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

    public LiveData<Integer> buyCar(Car car){
        MutableLiveData<Integer>isBought=new MutableLiveData<>();
        endPoints.buyCar(car.getRegistration_id()).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    int i=response.body().getIsSuccess();
                    if(i==1){
                        CarDatabase.executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                car.setQuantity(car.getQuantity()-1);
                                carDao.update(car);
                                Log.d(TAG, "onResponse: on buyCar(): "+car.toString());
                            }
                        });

                    }
                    isBought.postValue(i);
                }else{
                    Log.e(TAG, "onResponse: CarRoot call is unsuccessful. Response code: "+response.code()) ;

                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return isBought;
    }

    public void deleteAllCars(){
        CarDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                carDao.deleteAllCars();
            }
        });

    }
}
