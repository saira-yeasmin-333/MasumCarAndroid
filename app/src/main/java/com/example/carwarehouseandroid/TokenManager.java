package com.example.carwarehouseandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static int Mode=0;
    public static  String REFNAME="JWTTOKEN";
    public static  String KEY_USERNAME="username";
    public static  String KEY_JWT_TOKEN="jwttoken";
    private Context context;

    public TokenManager(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(REFNAME,Mode);
        editor=sharedPreferences.edit();
    }

    public void createSession(String username,String jwtvalue,int id ){
        Log.d("hi","are u there");
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_JWT_TOKEN,jwtvalue);
        editor.putInt("id",id);
        editor.commit();
    }

}
