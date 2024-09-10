package com.example.sortifyandroidapp.Activities.UserActivities;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sortifyandroidapp.Adapters.AllRequestsFAdapter;
import com.example.sortifyandroidapp.Adapters.ProductUserAdapter;
import com.example.sortifyandroidapp.Adapters.RequestsUserAdapter;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startActivityForResultLauncher;
    RecyclerView requestRecyclerView;
    RequestsUserAdapter requestsAdapter;
    Button sendRequestBtn, allRequestsBtn;
    FloatingActionButton returnBtn;
    List<Form> myRequestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_support);

        requestRecyclerView = findViewById(R.id.requestsRecyclerView);
        sendRequestBtn = findViewById(R.id.sendRequestBtn);
        allRequestsBtn = findViewById(R.id.myRequestsBtn);
        returnBtn = findViewById(R.id.backBtn2);

        startActivityForResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Refresh the RecyclerView data
                            fetchMyRequests();
                        }
                    }
                }
        );

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set up RecyclerView
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestsAdapter = new RequestsUserAdapter(myRequestList); // Pass any necessary data
        requestRecyclerView.setAdapter(requestsAdapter);

        // Fetch data from API
        fetchMyRequests();
        // Set up button click listener
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupportActivity.this, SendSupportMsgActivity.class);
                startActivityForResultLauncher.launch(intent);
            }
        });
    }

    private void fetchMyRequests() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        InterfaceUserAPIService apiService = Connection.getClient().create(InterfaceUserAPIService.class);
        Call<List<Form>> call = apiService.getMyRequests(jwt);
        call.enqueue(new Callback<List<Form>>() {
            @Override
            public void onResponse(@NonNull Call<List<Form>> call, @NonNull Response<List<Form>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Form> forms = response.body();
                    Log.d(TAG, "--------onResponse: " + forms);
                    requestsAdapter.setForms(forms); // Update your adapter with the new data
                } else {
                    Log.e(TAG, "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Form>> call, @NonNull Throwable t) {
                Log.e(TAG, "API request failed", t);
            }
        });
    }
}