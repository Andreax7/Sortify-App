package com.example.sortifyandroidapp.Endpoints;

import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.ContainerLocation;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.Recycled;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.Models.UserPassword;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    @GET("product/all/{tid}")
    Call<List<Product>> getProductsByType(@Header("x-access-token") String token, @Path(value="tid", encoded=true) String tid);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("locations/all")
    Call<List<Container>> getAllContainerLocations(@Header("x-access-token") String token);


   // @Headers({
   //         "Accept: */* ",
  //          "User-Agent: Retrofit-App",
 //           "content-type: application/json"
//    })
//    @GET("locations/{typeId}")
//    Call<List<ContainerLocation>> getContainersByType(@Header("x-access-token") String token);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("product/{barcode}")
    Call<Product> getScannedProduct(@Header("x-access-token") String token, @Path(value="barcode", encoded=true) String barcode);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("product/filter/{pid}")
    Call<List<Product>> getProductDataById(@Header("x-access-token") String token, @Path(value="pid", encoded=true) String pid);



    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("profile/all/stats")
    Call<List<Recycled>> getAllRecycledData(@Header("x-access-token") String token);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("profile/stat")
    Call<List<Recycled>> getMyRecycledData(@Header("x-access-token") String token);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("profile/throw")
    Call<ResponseBody> throwMyTrash(@Header("x-access-token") String token, @Body HashMap<String, Integer> containerId);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("profile/requests")
    Call<List<Form>> getMyRequests(@Header("x-access-token") String token);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("profile/requests/send")
    Call<List<Form>> sendRequest(@Header("x-access-token") String token, @Body Form form);

    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("/profile/stats_per_user")
    Call<HashMap<String,Float>> getStatPerUser(@Header("x-access-token") String token);


}
