package com.if5b.iklanku.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utilities {
    private  static final  String BASE_URL = "https://myiklan.000webhostapp.com/laravel/api.php/userController/";
    public static final  String API_KEY = "Jqr6xuns091202";
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
}
