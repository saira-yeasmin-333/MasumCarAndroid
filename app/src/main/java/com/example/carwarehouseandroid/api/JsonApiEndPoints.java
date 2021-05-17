package com.example.carwarehouseandroid.api;

import androidx.room.Delete;

import com.example.carwarehouseandroid.Model.AuthToken;
import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.Model.CarReg;
import com.example.carwarehouseandroid.Model.UserLogin;
import com.example.carwarehouseandroid.rootModel.CarRegister;
import com.example.carwarehouseandroid.rootModel.CarRoot;
import com.example.carwarehouseandroid.rootModel.RootcarId;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface JsonApiEndPoints {
    @POST("api/users/manufacturer/login")
    Call<AuthToken> login (@Body UserLogin login);

    @POST("api/users/manufacturer/login")
    Call<JsonObject> login2 (@Body UserLogin login);
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("api/users/manufacturer/car/getById")
    Call<RootcarId>carId(@QueryMap Map<String, Integer> parameters, @Header("authorization") String authHeader);

    @GET("api/users/viewer/car/getById")
    Call<Car>CarId(@Body int id);

    @GET("api/users/viewer/car/getAll")
    Call<List<Car>>AllCar();

    @GET("api/users/viewer/car/getAll")
    Call<CarRoot>AllCar1();

    @GET("api/users/manufacturer/car/getAll")
    Call<CarRoot>AllMCars( @Header("authorization") String authHeader);

    @POST("api/users/manufacturer/car/register")
    Call<CarRegister>addNewCar(@Body Car car, @Header("authorization") String authHeader);

    @PUT("api/users/manufacturer/car/update")
    Call<CarRegister>updateCar(@Body Car car, @Header("authorization") String authHeader);

    @DELETE("api/users/manufacturer/car/delete")
    Call<CarRegister>deleteCar(@QueryMap Map<String, Integer> parameters, @Header("authorization") String authHeader);


}
