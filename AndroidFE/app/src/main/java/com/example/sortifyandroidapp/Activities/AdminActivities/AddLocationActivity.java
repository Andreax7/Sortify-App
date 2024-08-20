package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddLocationActivity extends AppCompatActivity {
    //private GoogleMap mMap;
    //private Marker marker;
    private Spinner spinnerTypes;
    private ArrayList<String> trashTypeNames = new ArrayList<>(); // Initialize the ArrayList
    private ArrayList<TrashType> trashTypeList = new ArrayList<>(); // Initialize the ArrayList
    private Button buttonSave;
    private String selectedTrashType;
    private Container newContainer;

    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        spinnerTypes = findViewById(R.id.spinner_types);
        buttonSave = findViewById(R.id.button_save);

        getTypesFromDB();

        // Initialize map
        //  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //  if (mapFragment != null) {
        //  mapFragment.getMapAsync(this);
        //  }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }


    /*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable user location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Set a marker click listener to get marker position
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });
    }
    */
    private void saveData() {
   /* if (marker == null) {
        Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
        return;
    }
*/
        selectedTrashType = spinnerTypes.getSelectedItem().toString();
        // LatLng selectedLocation = marker.getPosition();

        // Send data to API
        //  sendLocationToAPI(selectedLocation.latitude, selectedLocation.longitude, selectedTrashType);
    }

    private void sendLocationToAPI(double latitude, double longitude, String trashType) {
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService APIService = retrofit.create(InterfaceAdminAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        newContainer = new Container(Integer.valueOf(trashType), longitude, latitude);

        Call<ResponseBody> call = APIService.addContainerOnLocation(jwt, newContainer);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddLocationActivity.this, "Location saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddLocationActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(AddLocationActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTypesFromDB() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        // calling a method to get user data
        Call<List<TrashType>> call = userAPIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {

                if (response.code() == 400) {
                    Toast.makeText(AddLocationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    trashTypeList = (ArrayList<TrashType>) response.body();

                    for (TrashType type : trashTypeList) {
                        if (!type.typeName.isEmpty()) {
                            Log.d(TAG, "ADD: " + type.typeName);
                            trashTypeNames.add(type.typeName);
                        }
                    }
                    Log.d(TAG, "onResponse: " + trashTypeNames);

                    // Move this inside the response to ensure the adapter is set after data is fetched
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddLocationActivity.this, android.R.layout.simple_spinner_dropdown_item, trashTypeNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTypes.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(AddLocationActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
