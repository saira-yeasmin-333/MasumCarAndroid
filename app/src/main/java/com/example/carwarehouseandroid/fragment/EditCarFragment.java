package com.example.carwarehouseandroid.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.bumptech.glide.Glide;
import com.example.carwarehouseandroid.AWSConfiguration;
import com.example.carwarehouseandroid.Model.Car;
import com.example.carwarehouseandroid.Model.CarImage;
import com.example.carwarehouseandroid.Model.CarReg;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.Util;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.rootModel.CarRegister;
import com.example.carwarehouseandroid.viewModel.MainViewModel;

import java.io.FileNotFoundException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCarFragment extends Fragment {
    private View view;
    private TextView textView;
    private ImageView imageView;
    private EditText editText1,editText2,editText3,editText4,editText5,editText6,editText7,editText8;
    private Button button1,button2,button3;
    private String token,s1,s2,s3,s4,s5,s6,s7,s8;
    private int m_id;
    private int reg_id  = -1;
    private static final int GalleryPick = 100;
    private String make = "";
    private String model = "";
    private String color= "";
    private int price = -1;
    private int quantity = -1;
    private String image_name="";
    private int year = -1;
    private  String image="";
    private boolean mode=false;
    private MainViewModel mVM;
    private Uri imageUri;
    private Bitmap photo_bitmap=null;
    private String imageUrl=null;

    public static final String TAG="Editcarfragment->";
    private AWSConfiguration configuration;


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
            image= this.getArguments().getString("image");
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
        mVM = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getActivity().getApplication()))
                .get(MainViewModel.class);

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

        button1.setOnClickListener(v->{
            selectImage();
        });
        return view;
    }

    private void selectImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
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
            if(image!=null)
            Glide.with(getContext()).load(image).placeholder(R.drawable.icon).into(imageView);
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
            if(!mode){

                addCarNow();
            }

            else{
                editCar();
            }
        }

    }

    private void editCar() {

        Car car=new Car(Integer.parseInt(s1),Integer.parseInt(s4),Integer.parseInt(s6),Integer.parseInt(s7),m_id,s2,s3,s5,imageUrl);

        LiveData<Boolean>  i=mVM.updateCar(car);
        i.observe(getViewLifecycleOwner(), integer -> {
            if(integer){
                Util.showToast(getContext(),"car was updated successfully");
                Log.e(TAG, "onResponse: "+"car was updated successfully" );
                NavHostFragment.findNavController(EditCarFragment.this).popBackStack();
            }
            else{
                Util.showToast(getContext(),"car could not be updated");
            }

        });



    }

    private void addCarNow() {
        Log.e(TAG, "addCarNow: imageurl: "+imageUrl );
        Car car=new Car(Integer.parseInt(s1),Integer.parseInt(s4),Integer.parseInt(s6),Integer.parseInt(s7),m_id,s2,s3,s5,imageUrl);
        photo_bitmap=null;
        imageUrl=null;
        LiveData<Boolean> i=mVM.addCar(car);
        Log.e(TAG, "addCarNow: "+i );
        i.observe(getViewLifecycleOwner(), integer -> {
            if(integer){

                Log.e(TAG, "addCarNow: "+i );
                Util.showToast(getContext(),"car was added successfully");
                NavHostFragment.findNavController(EditCarFragment.this).popBackStack();
            }
            else{
                Util.showToast(getContext(),"car could not be added");
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
        imageView=view.findViewById(R.id.car_image_edit);
        button1=view.findViewById(R.id.chose_ing_btn);
        button2=view.findViewById(R.id.submit_btn);
        button3=view.findViewById(R.id.reset_btn_edit);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imageUri=data.getData();
            Log.e(TAG, "onActivityResult: "+imageUri );
            photo_bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
            imageView.setImageBitmap(photo_bitmap);
            uploadImage();
            Log.e(TAG, "onActivityResult: "+"after photo bitmap" );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        Log.e(TAG, "onActivityResult: "+"is called" );
    }



    private void uploadImage() {

        configuration= AWSConfiguration.getInstance(getContext());
        Log.e(TAG, "uploadImage: image: "+image );
        configuration.uploadPhoto(photo_bitmap);
/*
        if(image!=null&&mode){
            Log.e(TAG, "uploadImage: when image is not null"+image);
            configuration.deletePicture(image);
        }*/
        configuration.setOnClickListener(url -> {
            Log.e(TAG, "uploadImage: on setonclicklistener"+url );
             imageUrl=url;
        });
    }
}