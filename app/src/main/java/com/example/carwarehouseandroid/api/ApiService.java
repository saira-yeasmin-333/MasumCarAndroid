package com.example.carwarehouseandroid.api;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static ApiService mInstance;
    private final Retrofit retrofit;
    private final JsonApiEndPoints jsonApiEndPoints;

    private ApiService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://vj4z2680td.execute-api.ap-south-1.amazonaws.com/dev/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonApiEndPoints = retrofit.create(JsonApiEndPoints.class);
        Log.d("dieeeeeeeeee", "Retrofit Api is created successfully");
    }

    public static synchronized ApiService getInstance() {
        if (mInstance == null) {
            mInstance = new ApiService();
        }
        return mInstance;
    }

    public JsonApiEndPoints getJsonApiEndPoints() {
        return jsonApiEndPoints;
    }
}
