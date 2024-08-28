package com.example.sortifyandroidapp.Endpoints;

import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.ContainerLocation;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.Models.UserPassword;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface InterfaceUserAPIService {
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("profile")
    Call<User> getProfileData(@Header("x-access-token") String token);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("profile/update")
    Call<Object> updateProfileData(@Header("x-access-token") String token, @Body User user);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("profile/change_password")
    Call<Object> updateUserPassword(@Header("x-access-token") String token, @Body UserPassword password);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("type/alltypes")
    Call<List<TrashType>> getAllTypes(@Header("x-access-token") String token);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("product/all")
    Call<List<Product>> getAllProducts(@Header("x-access-token") String token);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("locations/all")
    Call<List<Container>> getAllContainerLocations(@Header("x-access-token") String token);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("locations/{typeId}")
    Call<List<ContainerLocation>> getContainersByType(@Header("x-access-token") String token);

}
