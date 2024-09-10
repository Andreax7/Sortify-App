package com.example.sortifyandroidapp.Activities.AdminActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.sortifyandroidapp.Listeners.OnFormClickListener;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This fragment shows a list of user requests with switch button to toggle between seen and unseen status
 *
 */

public class FragmentAllUserRequests extends Fragment implements OnFormClickListener {

    private RecyclerView requestsRecycler;
    private AllRequestsFAdapter formAdapter;
    private List<Form> allForms = new ArrayList<>();
    private List<Form> filteredForms = new ArrayList<>();
    private Switch seenSwitch;
    private Retrofit retrofit;
    private InterfaceAdminAPIService adminAPIService;

    public FragmentAllUserRequests() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new FragmentAllUserRequests();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrofit = Connection.getClient();
        adminAPIService = retrofit.create(InterfaceAdminAPIService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_user_requests, container, false);

        requestsRecycler = view.findViewById(R.id.requestListRecyclerView);
        formAdapter = new AllRequestsFAdapter(filteredForms, this);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestsRecycler.setAdapter(formAdapter);

        seenSwitch = view.findViewById(R.id.seenSwitch);
        seenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> filterForms(isChecked));

        getRequestsFromDB();

        return view;
    }

    @Override
    public void onFormClick(Form form) {
        Log.d("---FRAGMENT2", "On form click " + form.productName);
        Intent intent = new Intent(getContext(), FormDetailActivity.class);
        intent.putExtra("selectedForm", form);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getRequestsFromDB();
    }

    private void getRequestsFromDB() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Call<List<Form>> call = adminAPIService.getAllUserForms(jwt);
        call.enqueue(new Callback<List<Form>>() {
            @Override
            public void onResponse(@NonNull Call<List<Form>> call, @NonNull Response<List<Form>> response) {
                if(response.code() == 400 || response.code() == 403){
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    if(response.body() != null){
                        allForms.clear();
                        allForms.addAll(response.body());

                        filterForms(seenSwitch.isChecked());

                    } else {
                        Toast.makeText(getContext(), "NO USER REQUEST CURRENTLY", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Form>> call, @NonNull Throwable throwable) {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterForms(boolean showSeen) {
        filteredForms.clear();
        for (Form form : allForms) {
            if (form.seen == (showSeen ? 1 : 0)) {
                filteredForms.add(form);
            }
        }

        if (formAdapter == null) {
            formAdapter = new AllRequestsFAdapter(filteredForms, this);
            requestsRecycler.setAdapter(formAdapter);
        } else {
            formAdapter.setRequestList(filteredForms);
            formAdapter.notifyDataSetChanged();
        }
    }
}