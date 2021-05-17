package com.example.carwarehouseandroid.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.Model.CarReg;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.adapter.CarAdapter;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.repository.CarRepository;
import com.example.carwarehouseandroid.rootModel.CarRegister;
import com.example.carwarehouseandroid.rootModel.CarRoot;
import com.example.carwarehouseandroid.rootModel.RootcarId;
import com.example.carwarehouseandroid.viewModel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment->";
    private View view;
    private RecyclerView recyclerView;
    public  String token;
    private CarAdapter adapter;

    private EditText editText1,editText2,editText3;
    private FloatingActionButton button;
    private  List<Car>cars;

    private MainViewModel mVM;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        //data view model
        mVM = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getActivity().getApplication()))
                .get(MainViewModel.class);

        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);

       // AWSMobileClient.getInstance().initialize(getActivity().getApplicationContext()).execute();
        recyclerView = view.findViewById(R.id.home_manu_recyclerView);
        button=view.findViewById(R.id.add_car_btn);
        editText1=view.findViewById(R.id.searchby_reg_edit);
        editText2=view.findViewById(R.id.searchby_make_edit);
        editText3=view.findViewById(R.id.searchby_model_edit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        SharedPreferences sharedPreferences=getContext().getSharedPreferences(TokenManager.REFNAME,TokenManager.Mode);
        token=sharedPreferences.getString(TokenManager.KEY_JWT_TOKEN,"not found");
        adapter = new CarAdapter();
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");

        fetchData();

        adapter.setOnChildClickListener(new CarAdapter.OnChildClickListener() {
            @Override
            public void onDeleteButtonClicked(Car car,int position) {
                Log.d(TAG, "onViewCreated():onDeleteButtonClicked: ");
                Toast.makeText(getContext(),"Delete Button Clicked",Toast.LENGTH_SHORT).show();
                showDeleteDialogue(car,position);

            }

            @Override
            public void onEidtButtonClicked(Car currentCar) {
                Log.d(TAG, "onViewCreated():onEidtButtonClicked: ");
                Toast.makeText(getContext(),"Edit Button Clicked",Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("registration_id",currentCar.getRegistration_id());
                bundle.putString("make",currentCar.getMake());
                bundle.putString("model",currentCar.getModel());
                bundle.putInt("price",currentCar.getPrice());
                bundle.putInt("quantity",currentCar.getQuantity());
                bundle.putInt("year",currentCar.getYear());
                bundle.putString("colour",currentCar.getColour());

                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_editCarFragment,bundle,null,null);

            }
        });

        button.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_editCarFragment);
        });

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("filter",s.toString());
                ArrayList<Car> filterCAr = new ArrayList<>();
                if (cars!=null){
                    for (Car car: cars ){
                        String dat = s.toString();
                        if (String.valueOf(car.getRegistration_id()).contains(dat)) {
                            filterCAr.add(car);
                        }
                    }
                    //cars= filterCAr;
                    adapter.setCars(filterCAr);
                    adapter.notifyDataSetChanged();
                    // recyclerView.setAdapter(new CarAdapter());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Car> filterCAr = new ArrayList<>();
                if (cars!=null){
                    for (Car car: cars ){
                        String dat = s.toString();
                        if (String.valueOf(car.getMake()).contains(dat)) {
                            filterCAr.add(car);
                        }
                    }
                    //cars= filterCAr;
                    adapter.setCars(filterCAr);
                    adapter.notifyDataSetChanged();
                    // recyclerView.setAdapter(new CarAdapter());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Car> filterCAr = new ArrayList<>();
                if (cars!=null){
                    for (Car car: cars ){
                        String dat = s.toString();
                        if (String.valueOf(car.getModel()).contains(dat)&&(car.getMake().contains(editText2.getText().toString()))) {
                            filterCAr.add(car);
                        }
                    }
                    //cars= filterCAr;
                    adapter.setCars(filterCAr);
                    adapter.notifyDataSetChanged();
                    // recyclerView.setAdapter(new CarAdapter());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void showDeleteDialogue(Car currentCar,int i) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        Log.d("inside", "showCustomDialog: ");
        builder.setMessage("Are u sure u want to delete this")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCar(currentCar,i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        builder.show();
    }



    private void deleteCar(Car car,int position) {
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("id", car.getRegistration_id());
        parameters.put("userId", car.getM_id());
        ApiService.getInstance().getJsonApiEndPoints().deleteCar(parameters,"Bearer "+token).enqueue(new Callback<CarRegister>() {
            @Override
            public void onResponse(Call<CarRegister> call, Response<CarRegister> response) {
                if(response.isSuccessful()){
                    int i=response.body().getIsSuccess();
                    if(i==1){
                        Toast.makeText(getContext(),"car was deleted successfully",Toast.LENGTH_LONG).show();
                        // notify();
                        cars.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, cars.size());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<CarRegister> call, Throwable t) {

            }
        });
    }

    private void fetchData() {
        Log.d(TAG, "fetchData: fetching cars data.");
        mVM.getAllCars().observe(getViewLifecycleOwner(),carList -> {
            Log.d(TAG, "fetchData(): car list received from car repo.");
            cars=carList;
            adapter.setCars(cars);
            adapter.notifyDataSetChanged();
        });
    }


}