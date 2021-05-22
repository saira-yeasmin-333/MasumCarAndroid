package com.example.carwarehouseandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.carwarehouseandroid.adapter.CarAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class AWSConfiguration {
    public static final String TAG="AWSConfiguration->";
    private Context context;
    private static AWSConfiguration mInstance;
    private String ACCESS_KEY,SECRET_KEY,MY_BUCKET,OBJECT_KEY;
    private AmazonS3 s3;
    private getUrl listener;

    private AWSConfiguration(Context context) {
        this.context=context;

        ACCESS_KEY="";
        SECRET_KEY= "";
        MY_BUCKET= "car-bucket";
        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        s3 = new AmazonS3Client(credentials);
        java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
        s3.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        s3.setEndpoint("https://s3-ap-south-1.amazonaws.com/");
        Log.e(TAG, "AWSConfiguration: "+ "is it called?");
    }

    public static synchronized AWSConfiguration getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AWSConfiguration(context);
        }
        return mInstance;
    }

    public void uploadPhoto(Bitmap photo_bitmap){
        final ProgressDialog progressDialog =new ProgressDialog(context,R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle("Uploading Profile Image...");
        progressDialog.setMessage("Please wait while your profle image is being updated");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String file_name=System.currentTimeMillis()+"";


        OBJECT_KEY="images/"+file_name+".jpg";

        TransferUtility transferUtility = new TransferUtility(s3, context);


        File f = new File(context.getFilesDir(),"image.jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        photo_bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TransferObserver observer = transferUtility.upload(MY_BUCKET,OBJECT_KEY,f, CannedAccessControlList.PublicRead);
        final String finalFile_name = file_name;
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if(state.name().equals("COMPLETED")){
                    String imageUrl =  "https://car-bucket.s3.ap-south-1.amazonaws.com/images/"+ finalFile_name +".jpg";
                    progressDialog.dismiss();
                    Util.showToast(context,"Profile picture uploaded, check log for image URL.");
                    Log.e("image_url",imageUrl);
                    if(listener!=null){
                        listener.fetchUrl(imageUrl);
                    }
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
                Util.showToast(context,ex.getMessage());
                progressDialog.dismiss();
            }

        });
    }

    public interface getUrl{
        void fetchUrl(String url);
    }

    public void setOnClickListener(getUrl listener) {
        this.listener = listener;
    }

   public void deletePicture(String url){
       AmazonS3URI s3Uri = new AmazonS3URI(url);
       Log.e(TAG, "deletePicture: s3uri->"+s3Uri.getKey()+"," +s3Uri.getBucket()+","+s3Uri.getURI());
       String key=s3Uri.getKey();
       if(key==null){
           Log.e(TAG, "deletePicture: "+"no hope" );
       }else{
           Log.e(TAG, "deletePicture: "+key );
           new Thread(new Runnable() {
               @Override
               public void run() {
                   s3.deleteObject(s3Uri.getBucket(), s3Uri.getKey());
               }
           }).start();
       }

   }


}
