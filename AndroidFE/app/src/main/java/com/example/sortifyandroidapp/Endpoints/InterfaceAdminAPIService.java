package com.example.sortifyandroidapp.Endpoints;

import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.Models.User;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/*
 * Interface for admin CRUD requests - authorized routes
 * */

public interface InterfaceAdminAPIService {

    // Method for sending get request to get a list of all users
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("admin/users")
    Call<List<User>> getAllUsers(@Header("x-access-token") String token);

    //Method for sending get request -getting user forms as a response
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("admin/users/requests")
    Call<List<Form>> getAllUserForms(@Header("x-access-token") String token);

    //Post request for adding new trash type in DB
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("admin/type/add")
    Call<List<TrashType>> addNewTrashType(@Header("x-access-token") String token, @Body TrashType trashType);


    // Request with id as a parameter to delete trash type with given id
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @DELETE("admin/type/{id}")
    Call<List<TrashType>> deleteTrashType(@Header("x-access-token") String token,@Path(value="id", encoded=true) String id);

    // Request for updating trash type with id in param and body with new data
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @PUT("admin/type/{id}")
    Call<List<TrashType>> updateTrashType(@Header("x-access-token") String token, @Path(value="id", encoded=true) String id, @Body TrashType typeObj);

    //Post request for adding new product in DB
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("admin/product/add")
    Call<List<Product>> addNewProduct(@Header("x-access-token") String token, @Body Product product);

    // Request for updating existing product
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @PUT("admin/product/{id}")
    Call<ResponseBody> updateProduct(@Header("x-access-token") String token,@Path(value="id", encoded=true) String id, @Body User product);

    // Request with id as a parameter to delete product with given id
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @DELETE("admin/product/{id}")
    Call<List<Product>> deleteProduct(@Header("x-access-token") String token,@Path(value="id", encoded=true) String id);

    //Put request to activate/ deactivate users profile & set/ unset user as admin
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @PUT("admin/users/{uid}")
    Call<ResponseBody> changeUserStatus(@Header("x-access-token") String token, @Path(value="uid", encoded=true) Integer uid, @Body Integer active);

    // Changes User from Admin to User and revert
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @PUT("admin/users/{uid}")
    Call<ResponseBody> changeUserRole(@Header("x-access-token") String token,@Path(value="uid", encoded=true) Integer uid, @Body Integer role);


    //Put request to set/ unset users request status as seen/ unseen
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @PUT("admin/users/requests/{id}")
    Call<ResponseBody> changeRequestStatus(@Header("x-access-token") String token,@Path(value="id", encoded=true) String id, @Body String seen);

    // Get request for getting user form(request) with given id
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("admin/users/requests/{id}")
    Call<Form> getUserForm(@Header("x-access-token") String token,@Path(value="id", encoded=true) String id, @Body String seen);

    //Post request for adding new container location in DB
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @POST("admin/locations/add")
    Call<ResponseBody> addContainerOnLocation(@Header("x-access-token") String token, @Body Container container);


    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @GET("admin/containers")
    Call<List<Container>> getAllContainerLocations(@Header("x-access-token") String token);

    //Post request for adding new container location in DB
    @Headers({
            "Accept: */* ",
            "User-Agent: Retrofit-App",
            "content-type: application/json"
    })
    @PUT("admin/containers/{id}")
    Call<ResponseBody> editContainerLocation(@Header("x-access-token") String token, @Path(value="id", encoded=true) String id, @Body Container container);



}
