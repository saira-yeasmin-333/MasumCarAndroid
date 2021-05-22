package com.example.carwarehouseandroid.fragment;

import android.app.AlertDialog;
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

import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.Util;
import com.example.carwarehouseandroid.adapter.CarAdapter;
import com.example.carwarehouseandroid.adapter.CarViewerAdapter;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.rootModel.CarRoot;
import com.example.carwarehouseandroid.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewerFragment extends Fragment {

    private View view;
    private EditText editText1,editText2,editText3;
    private RecyclerView recyclerView;
    private MainViewModel mVM;
    private CarViewerAdapter adapter;
    private  List<Car>cars;
    public static final String TAG="ViewerFragment->";

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
        mVM = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getActivity().getApplication()))
                .get(MainViewModel.class);

        editText1=view.findViewById(R.id.searchby_reg_edit_1);
        editText2=view.findViewById(R.id.searchby_make_edit_1);
        editText3=view.findViewById(R.id.searchby_model_edit_1);
        recyclerView = view.findViewById(R.id.home_view_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new CarViewerAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchCars();
        adapter.setOnItemClickListener(car -> {
           showBoughtDialogue(car);
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
                    adapter.submitList(filterCAr);
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
                    adapter.submitList(filterCAr);
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
                    adapter.submitList(filterCAr);
                    adapter.notifyDataSetChanged();
                    // recyclerView.setAdapter(new CarAdapter());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void showBoughtDialogue(Car car) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        Log.d("inside", "showCustomDialog: ");
        builder.setMessage("Are u sure u want to buy this")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> buyCar(car))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void buyCar(Car car) {
        Log.e(TAG, "buyCar: in fragment" );
        LiveData<Integer>i=mVM.buyCar(car);
        i.observe(getViewLifecycleOwner(), integer -> {
            if(integer==1){
                Util.showToast(getContext(),"car was bought successfully");
                Log.e(TAG, "onResponse: "+"car was bought successfully" );
                //mVM.fetchAllCar();

            }else if(integer==5){
                Util.showToast(getContext(),"out of stock");
                Log.e(TAG, "onResponse: "+"out of stock" );
                //mVM.
            }else{
                Util.showToast(getContext(),"car could not be bought");
            }
        });
    }

    private void fetchCars() {
        Log.d(TAG, "fetchData: fetching cars data.");
        mVM.getAllViewerCars().observe(getViewLifecycleOwner(),carList -> {
            Log.d(TAG, "fetchData(): car list received from car repo.");
            //cars.clear();
            cars=carList;
            adapter.submitList(cars);
            adapter.notifyDataSetChanged();
        });
    }

}