package com.example.carwarehouseandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.rootModel.CarRoot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewerFragment extends Fragment {

    private View view;
    private TextView textView;

    public ViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_viewer, container, false);
        textView=view.findViewById(R.id.viewer_text);
        getCar();
        return view;
    }

    private void getCar() {

        /*ApiService.getInstance().getJsonApiEndPoints().CarId(6).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                if(response.isSuccessful()){
                    Car car=response.body();
                    textView.setText("car_id: "+car.getCar_id()+"\n"+
                            "m_id: "+car.getM_id()+"\n"+
                            "quantity: "+car.getQuantity()+"\n");
                    String s="";


                }else{
                    Log.d("error",response.message()+","+response.code());
                }

            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {

            }
        });*/
/*
        ApiService.getInstance().getJsonApiEndPoints().AllCar().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if(response.isSuccessful()){
                    List<Car>cars=response.body();
                    String s="";
                    for(Car car:cars){
                        s.concat("id: "+car.getRegistration_id()+"\n price: "+car.getPrice()+"\n");
                    }

                    textView.setText(s);

                }
                else{
                    Log.d("never","succeed");
                }


            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.d("never","succeed.......");
            }
        });*/


        ApiService.getInstance().getJsonApiEndPoints().AllCar1().enqueue(new Callback<CarRoot>() {
            @Override
            public void onResponse(Call<CarRoot> call, Response<CarRoot> response) {
                if(response.isSuccessful()){
                   /* List<Car>cars=response.body().getCars();
                    for(Car car:cars)
                    Log.d("ishhhhh",car.toString());*/
                }
            }

            @Override
            public void onFailure(Call<CarRoot> call, Throwable t) {

            }
        });
    }
}