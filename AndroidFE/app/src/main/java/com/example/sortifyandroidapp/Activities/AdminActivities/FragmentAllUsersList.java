package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sortifyandroidapp.Adapters.AllUsersFAdapter;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;

import com.example.sortifyandroidapp.Listeners.UserClickListener;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 *******************************************************************************************
 *
 * This fragment shows a list of users that are registered with role and status
 * It is possible to change user status and role by clicking on it
 *
// */
public class FragmentAllUsersList extends Fragment implements UserClickListener {

    private RecyclerView allUsersRecycler;
    private AllUsersFAdapter userAdapter;
    private List<User> allUsers = new ArrayList<>();

    Retrofit retrofit ;
    InterfaceAdminAPIService adminAPIService;

    public FragmentAllUsersList() {
    }

    public static FragmentAllUsersList newInstance() {
        FragmentAllUsersList fragment = new FragmentAllUsersList();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrofit = Connection.getClient();
        adminAPIService = retrofit.create(InterfaceAdminAPIService.class);

        getUsersFromDB();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_users_list, container, false);
        AllUsersFAdapter userAdapter = new AllUsersFAdapter(allUsers, this); // Assuming 'this' implements UserClickListener
        allUsersRecycler = view.findViewById(R.id.allUsersRecyclerView);
        allUsersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        allUsersRecycler.setAdapter(userAdapter);

        return view;
    }
/*
    private void initRecyclerView(View view) {
        allUsersRecycler = view.findViewById(R.id.allUsersRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        allUsersRecycler.setLayoutManager(layoutManager);
        AllUsersFAdapter userAdapter = new AllUsersFAdapter(allUsers, this);
        allUsersRecycler.setAdapter(userAdapter);
    }
*/

    //Gets User Data
    private void getUsersFromDB() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");
      //  Log.d("---FRAGMENT1 ","Getting data");

        Call<List<User>> call = adminAPIService.getAllUsers(jwt);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if(response.code()==400){
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    allUsers = response.body();
                    userAdapter = new AllUsersFAdapter(allUsers, FragmentAllUsersList.this);
                    allUsersRecycler.setAdapter(userAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable throwable) {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onUserRoleChanged(User user) {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Log.d(TAG, "------onUserRoleChanged: " + user.getRole()  );
        Call<ResponseBody> call = adminAPIService.changeUserRole(jwt, user.getUserId(), user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==400){
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Data successfully changed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onUserStatusChanged(User user) {

        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Call<ResponseBody> call = adminAPIService.changeUserStatus(jwt, user.getUserId(), user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==400 || response.code()==403){
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Data successfully changed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}