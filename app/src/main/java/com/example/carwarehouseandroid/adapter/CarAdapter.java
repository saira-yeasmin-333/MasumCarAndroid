package com.example.carwarehouseandroid.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.fragment.EditCarFragment;
import com.example.carwarehouseandroid.fragment.HomeFragment;
import com.example.carwarehouseandroid.rootModel.CarRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarHolder> {
    public static final String TAG = "CarAdapter->";
    private  List<Car> cars= new ArrayList<>();
    private OnChildClickListener listener;


    @NonNull
    @Override
    public CarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_item_layout, parent, false);
        return new CarHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull CarHolder holder, int position) {
        Car currentCar = cars.get(position);
        holder.textView1.setText(currentCar.getMake()+" "+currentCar.getModel());
        holder.textView2.setText(String.valueOf(currentCar.getRegistration_id()));
        holder.textView3.setText(String.valueOf(currentCar.getYear()));
        holder.textView4.setText(currentCar.getColour());
        holder.textView5.setText(String.valueOf(currentCar.getPrice()));
        holder.textView6.setText(String.valueOf(currentCar.getQuantity()));

        holder.imageView1.setOnClickListener(v -> {
          //  showDeleteDialogue(v.getContext(),currentCar,position);
            if(listener!=null){
                listener.onDeleteButtonClicked(currentCar,position);
            }else{
                Log.e(TAG, "onBindViewHolder(): set listener using setChildListener");
            }
        });

        holder.imageView2.setOnClickListener(v -> {
            if(listener!=null){
                listener.onEidtButtonClicked(currentCar);
            }else{
                Log.e(TAG, "onBindViewHolder(): set listener using setChildListener");
            }
        });

    }


    @Override
    public int getItemCount() {
        return cars.size();
    }

    public void setCars(List<Car> cars) {
        this.cars= cars;
        notifyDataSetChanged();
    }

    class CarHolder extends RecyclerView.ViewHolder{
        private TextView textView1,textView2,textView3,textView4,textView5,textView6;
        private ImageView imageView1,imageView2;

        public CarHolder(@NonNull View itemView) {
            super(itemView);
            textView1=itemView.findViewById(R.id.make_model_home_manu);
            textView2=itemView.findViewById(R.id.reg_id_manu_home);
            textView3=itemView.findViewById(R.id.year_manu_home);
            textView4=itemView.findViewById(R.id.color_manu_home);
            textView5=itemView.findViewById(R.id.price_manu_home);
            textView6=itemView.findViewById(R.id.quantity_manu_home);
            imageView1=itemView.findViewById(R.id.delete_manu_home);
            imageView2=itemView.findViewById(R.id.edit_manu_home);

        }
    }

    public interface OnChildClickListener {
        void onDeleteButtonClicked(Car car,int i);
        void onEidtButtonClicked(Car car);
    }
    public void setOnChildClickListener(OnChildClickListener listener) {
        this.listener = listener;
    }
}
