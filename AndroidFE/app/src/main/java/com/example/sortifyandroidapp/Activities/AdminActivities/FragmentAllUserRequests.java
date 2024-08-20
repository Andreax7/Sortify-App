package com.example.sortifyandroidapp.Activities.AdminActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.sortifyandroidapp.Adapters.AllRequestsFAdapter;
import com.example.sortifyandroidapp.Adapters.AllUsersFAdapter;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAllUserRequests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAllUserRequests extends Fragment {

    private RecyclerView requestsRecycler;
    private AllRequestsFAdapter formAdapter;
    private Switch seenSwitch;
    private List<Form> allForms = new ArrayList<>();
    private List<Form> filteredFormsByStatus = new ArrayList<>();

    // gets the connection and creates an instance for retrofit endpoint api class
    Retrofit retrofit = Connection.getClient();
    InterfaceAdminAPIService adminAPIService = retrofit.create(InterfaceAdminAPIService.class);


    public FragmentAllUserRequests() {
        // Required empty public constructor
    }
    

    public static Fragment newInstance() {
        FragmentAllUserRequests fragment = new FragmentAllUserRequests();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getRequestsFromDB();
       // seenSwitch = seenSwitch.findViewById(R.id.seenSwitch);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
   
        View view = inflater.inflate(R.layout.fragment_all_user_requests, container, false);
        
        AllRequestsFAdapter formAdapter = new AllRequestsFAdapter(allForms);
        requestsRecycler = view.findViewById(R.id.requestListRecyclerView);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestsRecycler.setAdapter(formAdapter);

        return view;
    }

    private void getRequestsFromDB() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");
        Log.d("---FRAGMENT2 ","Getting data");
        // calling a method to get forms data
        Call<List<Form>> call = adminAPIService.getAllUserForms(jwt);
        call.enqueue(new Callback<List<Form>>() {
            @Override
            public void onResponse(@NonNull Call<List<Form>> call, @NonNull Response<List<Form>> response) {
                if(response.code()==400 || response.code()==403){
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    if(response.body() != null){
                        allForms = response.body();
                        formAdapter = new AllRequestsFAdapter(allForms);
                        //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        //allUsersRecycler.setLayoutManager(layoutManager);
                        requestsRecycler.setAdapter(formAdapter);
                    }
                    else{
                        Toast.makeText(getContext(), " NO USER REQUEST CURRENTLY ", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Form>> call, @NonNull Throwable throwable) {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}