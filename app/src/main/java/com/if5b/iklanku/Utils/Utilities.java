package com.if5b.iklanku.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utilities {
    private static final String PREFERENCE_FILE_KEY ="myAppPreference";
    private  static final  String BASE_URL = "https://projectpab.000webhostapp.com/iklan/index.php/MobileControl/";
    public static final  String API_KEY = "iklanaja";
    private static Retrofit retrofit;

    public  static  Retrofit getRetrofit(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static  void clearUser (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(API_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("xUsername", null);
        editor.apply();
    }

    public static void setValue(Context context, String xPr, String xV){
        SharedPreferences sharedPreferences = context.getSharedPreferences(API_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(xPr, xV);
        editor.commit();
    }

    public static String getValue(Context context, String xPr){
        SharedPreferences sharedPreferences = context.getSharedPreferences(API_KEY, Context.MODE_PRIVATE);
        String xV = sharedPreferences.getString(xPr, null);
        return xV;
    }

    public static boolean checkValue(Context context, String xPr){
        SharedPreferences sharedPreferences = context.getSharedPreferences(API_KEY, Context.MODE_PRIVATE);
        String xV = sharedPreferences.getString(xPr, null);
        if (xV != null){
            return  true;
        }else{
            return  false;
        }
    }
}
