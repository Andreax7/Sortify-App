package com.example.sortifyandroidapp;

import android.util.Log;

import com.example.sortifyandroidapp.Endpoints.InterfaceIPEmulator;
import com.example.sortifyandroidapp.Endpoints.InterfaceIPMoto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* This is API Client class dedicated to handling network requests
*   defines a method to CREATE and RETURN a Retrofit instance
*
*  Connection class is used to get the Retrofit instance in any other class
*       (to make network requests.)
* */
public class Connection {
    // Set local or emulator url depending on usage
    private static final String LOCAL_URL = InterfaceIPMoto.LOCAL_URL;
    private static final String EMULATOR_URL = InterfaceIPEmulator.EMULATOR_URL; // Trailing slash is needed

    private static Gson gson;
    private static Retrofit retrofit;


    public static Retrofit getClient(){
        //Log.d("DEBUG"," URL CHECK (in Connection.java) ---> " + EMULATOR_URL);
        if(retrofit == null){
            gson = new GsonBuilder().setLenient().create();
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            // creating a retrofit builder and passing base url
            // as the data is send in json format -Gson converter factory needs to be added
            //
            retrofit = new Retrofit.Builder()
                    .baseUrl(EMULATOR_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

        }
        return retrofit;
    }


}
