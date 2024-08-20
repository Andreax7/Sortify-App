package com.example.sortifyandroidapp.Endpoints;

import com.example.sortifyandroidapp.Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
/*
* Interface for login/signup POST requests
* */
public interface InterfaceAPIAuthService {
/*
 Send POST request with fields as request body params
*/

    // annotating post request to send data to API
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("auth/signup")
    Call<String> signup(@Body User user);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("auth/login")
    Call<Object> login(@Body User user);
}
