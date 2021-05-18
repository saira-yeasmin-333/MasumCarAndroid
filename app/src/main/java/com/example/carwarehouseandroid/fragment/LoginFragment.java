package com.example.carwarehouseandroid.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.bumptech.glide.util.Util;
import com.example.carwarehouseandroid.Model.AuthToken;
import com.example.carwarehouseandroid.Model.UserLogin;
import com.example.carwarehouseandroid.R;
import com.example.carwarehouseandroid.TokenManager;
import com.example.carwarehouseandroid.api.ApiService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class LoginFragment extends Fragment {
    private View view;
    public static final String TAG="Logfrag->";
    private Spinner spinner;
    private Button button1,button2,button3;
    private ImageView imageView;
    private static final int GalleryPick = 1;
    EditText editText1,editText2;
    private TokenManager tokenManager;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private Bitmap photo_bitmap;
    AmazonS3 s3;
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
        button3=view.findViewById(R.id.upload_btn);
        imageView=view.findViewById(R.id.car_login);
        editText1=view.findViewById(R.id.email_login);
        editText2=view.findViewById(R.id.password_login);
        loadingBar=new ProgressDialog(getContext());
        tokenManager=new TokenManager(getActivity().getApplicationContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

        Log.d("spinner",spinner.getSelectedItem().toString());
       // Toast.makeText(getContext(),spinner.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button2.setOnClickListener(v -> {
               String email=editText1.getText().toString();
               String password=editText2.getText().toString();
               String type=spinner.getSelectedItem().toString();
               UserLogin  obj=new UserLogin(email,password,type);

               loginApiCall(obj);
              // loginApiCall2(obj);
        });

        button1.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_viewerFragment);
        });

        button3.setOnClickListener(v -> {
            uploadImage();
        });
    }

    private void loginApiCall2(UserLogin obj) {
        ApiService.getInstance().getJsonApiEndPoints().login2(obj).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject object=new JsonObject();
                    object=response.body();
                    Log.d("is auth?",object.get("access_token").toString());
                    //tokenManager.createSession(object.get("username").toString(),object.get("access_token").toString());
                     SharedPreferences sharedPreferences=getContext().getSharedPreferences(TokenManager.REFNAME,TokenManager.Mode);
                    Log.d("is create?",sharedPreferences.getString(TokenManager.KEY_JWT_TOKEN,"not found"));
                    NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_homeFragment);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void loginApiCall(UserLogin obj) {
        ApiService.getInstance().getJsonApiEndPoints().login(obj).enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if(response.isSuccessful()){
                    AuthToken authToken=response.body();
                    Log.d("is auth?",authToken.toString());
                    Log.d(":is real",response.body().toString());
                    tokenManager.createSession(authToken.getUsername(),authToken.getAccess_token(),authToken.getUserId());
                    Log.d("is create?",TokenManager.KEY_JWT_TOKEN);
                    NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_homeFragment);
                }
                else{
                    Log.d("kutta.......","dont know");
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                Log.d("kutta.......",t.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {

            //CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(data.getData()==null){
                Log.d("is success","no");
            }
            else{
                imageUri = data.getData();
                try {
                    photo_bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                   // photo_bitmap = Util.getResizedBitmap(photo_bitmap,Constants.MAX_IMAGE_DIM);
                    imageView.setImageBitmap(photo_bitmap);
                    uploadImage();
                   // photo_select.setText("Change Photo");
                   // photo_select.setCompoundDrawablesWithIntrinsicBounds(R.drawable.swap_icon, 0, 0, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }


               // Log.d("is success","yes");
            }


            // imageView.setImageURI(imageUri);


        }
    }

    private void uploadImage() {
        loadingBar.setTitle("Update Profile");
        loadingBar.setMessage("Please wait while we are updating profile picture");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if(imageUri!=null){
            Log.d("login image","successful");
          //loadingBar.dismiss();

            String file_name=System.currentTimeMillis()+"";

            String      ACCESS_KEY="AKIA6L6OKQTGIIGJTFNJ",
                    SECRET_KEY= "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",
                    MY_BUCKET= "mehrabshoitan",
                    OBJECT_KEY=file_name+".jpg";



            AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
            Log.e(TAG, "uploadImage: "+credentials.getAWSAccessKeyId()+" "+credentials.getAWSSecretKey());
            AmazonS3 s3 = new AmazonS3Client(credentials);
            java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
            s3.setRegion(Region.getRegion(Regions.US_EAST_2));




            s3.setEndpoint("https://s3-us-east-2.amazonaws.com/");


            s3.setRegion(Region.getRegion(Regions.US_EAST_1));
/// here list of buckets which are full of evils don t permit accesss
            /*List<Bucket> buckets=s3.listBuckets();
            for(Bucket bucket:buckets){
                Log.e("Bucket ","Name "+bucket.getName()+" Owner "+bucket.getOwner()+ " Date " + bucket.getCreationDate());
            }*/
            TransferUtility transferUtility = new TransferUtility(s3, getContext());


            File f =  new File(getActivity().getCacheDir(),"image.jpg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            photo_bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos ;

            Log.e(TAG, "uploadImage: before trying " );
            try {
                fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                Log.e(TAG, "in try " );
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("amazon",e.getMessage());
            }

            Log.e(TAG, "uploadImage: file prperty"+f.length()+","+f.exists());



            TransferObserver observer = transferUtility.upload(MY_BUCKET,OBJECT_KEY,f, CannedAccessControlList.PublicRead);

            //final JSONObject newJsonObject=jsonObject;
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    // do something
                    if(state.name().equals("COMPLETED")){

                        Log.e(TAG, "onStateChanged: in on state changed" );
/*
                        String imageUrl = null;
                        try {
                            imageUrl = configData.getString("aws_link")+ Master.getInstance().getUid()+"/"+ finalFile_name +".jpg";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingBar.dismiss();
                        try {
                            Master.getInstance().updateProfileString("image",imageUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Util.showToast(getActivity().getApplicationContext(),"Profile picture updated");*/
                    }


                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    Log.e(TAG, "uploadImage: in progress "+bytesCurrent );
                }


                @Override
                public void onError(int id, Exception ex) {
                    // do something
                   // Util.showToast(getContext(),ex.getMessage());

                    loadingBar.dismiss();
                }

            });
        }

    }
}