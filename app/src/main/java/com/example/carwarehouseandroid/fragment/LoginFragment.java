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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.example.carwarehouseandroid.Model.AuthToken;
import com.example.carwarehouseandroid.Model.UserLogin;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.ApiService;
import com.example.carwarehouseandroid.Util;
import com.example.carwarehouseandroid.repository.CarRepo;
import com.example.carwarehouseandroid.repository.CarRepository;
import com.example.carwarehouseandroid.viewModel.MainViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private View view;
    public static final String TAG="Logfrag->";
    private Spinner spinner;
    private Button button1,button2;
    private ImageView imageView;
    private static final int GalleryPick = 1;
    EditText editText1,editText2;
    private TokenManager tokenManager;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private Bitmap photo_bitmap;
    private MainViewModel mVM;
    public LoginFragment() {
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
        view= inflater.inflate(R.layout.fragment_login, container, false);

        spinner=view.findViewById(R.id.role_spinner);
        button1=view.findViewById(R.id.viewer_login_btn);
        button2=view.findViewById(R.id.login_btn);
        imageView=view.findViewById(R.id.car_login);
        editText1=view.findViewById(R.id.email_login);
        editText2=view.findViewById(R.id.password_login);
        loadingBar=new ProgressDialog(getContext());
        tokenManager=new TokenManager(getActivity().getApplicationContext());

        gotoHome();

        mVM = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getActivity().getApplication()))
                .get(MainViewModel.class);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        imageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GalleryPick);
        });

        Log.d("spinner",spinner.getSelectedItem().toString());

        return view;
    }

    private void gotoHome() {
        SharedPreferences sharedPreferences=getContext().getSharedPreferences(TokenManager.REFNAME,TokenManager.Mode);
        String token=sharedPreferences.getString(TokenManager.KEY_JWT_TOKEN,"not found");
        String username=sharedPreferences.getString(TokenManager.KEY_USERNAME,"not found");
        if(!token.equals("not found")&&!username.equals("not found")){
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_homeFragment);

        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button2.setOnClickListener(v -> {
               String email=editText1.getText().toString();
               String password=editText2.getText().toString();
               String type=spinner.getSelectedItem().toString();
               if(email.isEmpty()){
                   editText1.setError("Please enter your email address");
                   editText1.requestFocus();
               }
               if(password.isEmpty()){
                   editText2.setError("Please enter your password");
                   editText2.requestFocus();
               }
               else{
                   mVM.deleteAllCars();
                   UserLogin  obj=new UserLogin(email,password,type);
                   ProgressDialog progressDialog=new ProgressDialog(getContext());
                   progressDialog.setTitle("Login...");
                   progressDialog.setMessage("Please wait while you are authenticated");
                   progressDialog.setCanceledOnTouchOutside(false);
                   progressDialog.show();
                   mVM.loginUser(obj, s -> {
                       mVM.setToken(s);
                       progressDialog.dismiss();
                       Util.showToast(getContext(),"You have successfully logged in");
                       NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_homeFragment);
                   });


               }


        });

        button1.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_viewerFragment);

        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imageUri=data.getData();
            photo_bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(photo_bitmap);
    }
}