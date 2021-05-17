package com.example.carwarehouseandroid.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.Model.CarReg;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.rootModel.CarRegister;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCarFragment extends Fragment {
    private View view;
    private TextView textView;
    private ImageView imageView;
    private EditText editText1,editText2,editText3,editText4,editText5,editText6,editText7,editText8;
    private Button button1,button2,button3;
    private String token,s1,s2,s3,s4,s5,s6,s7;
    private int m_id;
    private int reg_id  = -1;
    private String make = "";
    private String model = "";
    private String color= "";
    private int price = -1;
    private int quantity = -1;
    private int year = -1;
    private boolean mode=false;
    public EditCarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            reg_id=this.getArguments().getInt("registration_id",-1);
            make = this.getArguments().getString("make");
            model = this.getArguments().getString("model");
            color= this.getArguments().getString("colour");
            price= this.getArguments().getInt("price");
            quantity=this.getArguments().getInt("quantity");
            year=this.getArguments().getInt("year");
            mode=true;
        }
        else{
            mode=false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit_car, container, false);
        findAllViewId();
        if(mode)
        textView.setText("Edit "+make+" "+model);
        else textView.setText("Add a new Car");
        SharedPreferences sharedPreferences=getContext().getSharedPreferences(TokenManager.REFNAME,TokenManager.Mode);
        token=sharedPreferences.getString(TokenManager.KEY_JWT_TOKEN,"not found");
        m_id=sharedPreferences.getInt("id",-1);
        button2.setOnClickListener(v -> {
            addcar();
        });
        button3.setOnClickListener(v -> {
            resetEverything();
        });
        return view;
    }

    private void resetEverything() {
        if(mode){
            editText2.setText("");
            editText3.setText("");
            editText4.setText("");
            editText5.setText("");
            editText6.setText("");
            editText7.setText("");
        }else{
            editText1.setText("");
            editText2.setText("");
            editText3.setText("");
            editText4.setText("");
            editText5.setText("");
            editText6.setText("");
            editText7.setText("");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(reg_id != -1 &&!make.equals("") && !model.equals("") && year!=-1 && price!=-1 && quantity!=-1 && !color.equals(""))
        {
            editText1.setText(String.valueOf(reg_id));
            editText1.setEnabled(false);
            editText2.setText(make);
            editText3.setText(model);
            editText4.setText(String.valueOf(year));
            editText5.setText(color);
            editText6.setText(String.valueOf(price));
            editText7.setText(String.valueOf(quantity));
            mode=true;
        }
        else mode=false;

    }

    private void addcar() {
        s1=editText1.getText().toString();
        s2=editText2.getText().toString();
        s3=editText3.getText().toString();
        s4=editText4.getText().toString();
        s5=editText5.getText().toString();
        s6=editText6.getText().toString();
        s7=editText7.getText().toString();
        if(s1.isEmpty()){
            editText1.setError("please enter reg id");
            editText1.requestFocus();
        }
        if(s2.isEmpty()){
            editText2.setError("please enter car make");
            editText2.requestFocus();
        }
        if(s3.isEmpty()){
            editText3.setError("please enter car model");
            editText3.requestFocus();
        }
        if(s4.isEmpty()){
            editText4.setError("please enter year");
            editText4.requestFocus();
        }
        if(s5.isEmpty()){
            editText5.setError("please enter colour");
            editText5.requestFocus();
        }
        if(s6.isEmpty()){
            editText6.setError("please enter price");
            editText6.requestFocus();
        }
        if(s7.isEmpty()){
            editText7.setError("please enter quantity");
            editText7.requestFocus();
        }
        else{
            if(!mode)
            addCarNow();
            else{
                editCar();
            }
        }

    }

    private void editCar() {
        Car car=new Car(Integer.parseInt(s1),Integer.parseInt(s4),Integer.parseInt(s6),Integer.parseInt(s7),m_id,s2,s3,s5);
        ApiService.getInstance().getJsonApiEndPoints().updateCar(car,"Bearer "+token).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    int i=response.body().getIsSuccess();
                    if(i==1){
                        Toast.makeText(getActivity().getApplicationContext(),"car was updated successfully",Toast.LENGTH_LONG).show();
                        NavHostFragment.findNavController(EditCarFragment.this).popBackStack();
                    }
                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {

            }
        });
    }

    private void addCarNow() {
        Car car=new Car(Integer.parseInt(s1),Integer.parseInt(s4),Integer.parseInt(s6),Integer.parseInt(s7),m_id,s2,s3,s5);
        ApiService.getInstance().getJsonApiEndPoints().addNewCar(car,"Bearer "+token).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    int i=response.body().getIsSuccess();
                    if(i==1){
                        Toast.makeText(getActivity().getApplicationContext(),"car was added successfully",Toast.LENGTH_LONG).show();
                        NavHostFragment.findNavController(EditCarFragment.this).popBackStack();
                    }
                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {

            }
        });
    }

    private void findAllViewId() {
        textView=view.findViewById(R.id.edit_first_text);
        editText1=view.findViewById(R.id.reg_id_edit);
        editText2=view.findViewById(R.id.car_make_edit);
        editText3=view.findViewById(R.id.car_model_edit);
        editText4=view.findViewById(R.id.year_edit);
        editText5=view.findViewById(R.id.color_edit);
        editText6=view.findViewById(R.id.price_edit);
        editText7=view.findViewById(R.id.quantity_edit);
        editText8=view.findViewById(R.id.edit_image_name);
        imageView=view.findViewById(R.id.car_image_edit);
        button1=view.findViewById(R.id.chose_ing_btn);
        button2=view.findViewById(R.id.submit_btn);
        button3=view.findViewById(R.id.reset_btn_edit);
    }
}