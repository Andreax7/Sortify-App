package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.MapInterface;
import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    Marker marker;
    private Spinner spinnerTypes;
    private Switch switchFilter;
    private ArrayList<String> trashTypeNames = new ArrayList<>(); // Initialize the ArrayList
    private ArrayList<TrashType> trashTypeList = new ArrayList<>(); // Initialize the ArrayList
    private Button buttonEdit, buttonNext;
    private String selectedTrashType;
    private Container newContainer;
    private Container editedContainer;
    private Container clickedContainer;
    private Marker myLocationMarker;
    private LatLng selectedLocation;
    private LatLng newContainerLocation;

    // Global variable to store all container data
    private ArrayList<Container> allContainers = new ArrayList<>();

    // New variable to store selected container ID
    private Integer selectedContainerId = null; // use Integer instead of int to allow null values

    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class); // to catch trash types
    InterfaceAdminAPIService adminAPIService = retrofit.create(InterfaceAdminAPIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        spinnerTypes = findViewById(R.id.spinner_types);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonNext = findViewById(R.id.buttonNext);
        // Initialize switch button
        switchFilter = findViewById(R.id.ToggleBtn);

        getTypesFromDB();
        getContainersFromAPI(); // Fetch existing containers from the API

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewContainerData();
            }
        });

        buttonEdit.setEnabled(false);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Container container : allContainers) {
                    if(container.getContainerId() == selectedContainerId){
                        clickedContainer = container;
                    }
                }
                editData();
                Log.d(TAG, "--onMarkerClick: "+ selectedContainerId+ " " + newContainerLocation);
            }
        });

        // Set the listener for the switch button
        switchFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getFilteredContainersFromAPI();  // Fetch filtered containers when switch is on
            } else {
                getContainersFromAPI();  // Fetch all containers when switch is off
            }
        });

        // Initialize the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void editData() {

        // Inflate the popup window layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_location, null);

        Spinner spinnerEditTypes = dialogView.findViewById(R.id.spinner_edit_types);
        Switch switchEditFilter = dialogView.findViewById(R.id.switch_edit_filter);
        TextView containerType = dialogView.findViewById(R.id.contanerType);

        // Set up the spinner with trash types
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, trashTypeNames);
        spinnerEditTypes.setAdapter(adapter);
        // Set the Text View with data of container type
        for(TrashType type : trashTypeList){
            if(clickedContainer.getTypeId().equals(type.typeId)){
                containerType.setText("Container type: "+ type.typeName);
            }
        }
        // Build and show the dialog
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Edit Container")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get selected trash type from spinner
                    String editedTrashTypes = spinnerEditTypes.getSelectedItem().toString();


                    // Get switch state
                    Integer isFilterOn;
                    if(switchEditFilter.isChecked()){
                        isFilterOn = 1;
                    }
                    else{
                        isFilterOn = 0;
                    }

                    for (TrashType type : trashTypeList) {
                        if (type.typeName.equals(editedTrashTypes)) {
                            editedContainer = new Container(selectedContainerId, Integer.valueOf(type.typeId), isFilterOn, newContainerLocation.longitude, newContainerLocation.latitude);
                            // Perform save logic
                            saveEditedData();
                            return;
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void saveEditedData() {
        // Send edited location to api if all fields are ok
        Log.d(TAG, "--saveEditedData: " + editedContainer.getTypeId() +" "+ editedContainer.getLongitude()+" " + editedContainer.getLatitude());
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Call<ResponseBody> call = adminAPIService.editContainerLocation(jwt, editedContainer.getContainerId().toString(), editedContainer);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddLocationActivity.this, "Location saved successfully", Toast.LENGTH_SHORT).show();
                    getContainersFromAPI(); // Refresh container data
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


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // Enable user location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /* Set a marker click listener to get marker position and update selected container ID
          if the existed marker is clicked - this allows admin to edit data of the selected marker */
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                if (title != null && title.startsWith("Container ID: ")) {
                    buttonEdit.setEnabled(true);
                    // Extract container ID from the marker title
                    selectedContainerId = Integer.parseInt(title.replace("Container ID: ", "").trim());
                    newContainerLocation = marker.getPosition();
                    // Enable dragging for this marker
                    marker.setDraggable(true);
                } else {
                    buttonEdit.setEnabled(false);
                    Toast.makeText(AddLocationActivity.this, "Container not valid.", Toast.LENGTH_SHORT).show();
                }
                return false; // return false to allow default behavior (like camera centering on the marker)
            }
        });

        // RIGHT BUTTON LOCATION FUNCTIONALITY
        gMap.setMyLocationEnabled(true);
        gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LatLng loc = new LatLng(43.52328, 16.45060);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
                return true;
            }
        });
        // Add markers for all containers
        addMarkersForAllContainers();

        getDeviceLocation(new MapInterface() {
            @Override
            public void onLocationAvailable(LatLng location) {
                LatLng myLocation = new LatLng(43.52328, 16.45060);
                myLocationMarker = gMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));
                float zoomLevel = 15.0f; // Set zoom level (1.0f to 20.0f)
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                gMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                // Called when the user starts dragging the marker
                newContainerLocation = marker.getPosition();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // Called repeatedly while the user is dragging the marker
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Update selected location with the new marker position
                newContainerLocation = marker.getPosition();
                Toast.makeText(AddLocationActivity.this, "Marker moved to: " + newContainerLocation, Toast.LENGTH_SHORT).show();
            }
        });

        // This Map listener creates a new Marker (marks the location on a map to be added as a new container)
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }
                marker = gMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location").draggable(true));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
                selectedLocation = latLng;
                Log.d("---LOCATION", String.valueOf(marker.getPosition().latitude) + " " + String.valueOf(marker.getPosition().longitude));
            }
        });
    }

    private void getDeviceLocation(final MapInterface callback) {
        if (ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //HARDCODED FIRST LOCATION DUE TO EMULATOR
                    if (location != null) {
                        LatLng res = new LatLng(43.52328, 16.45060);
                        callback.onLocationAvailable(res);
                    } else {
                        Toast.makeText(AddLocationActivity.this, "Unable to find location.", Toast.LENGTH_SHORT).show();
                    }
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            });
        }
    }

    private void saveNewContainerData() {
        if (marker == null || spinnerTypes.getSelectedItem() == null) {
            Toast.makeText(this, "Please input correct information.", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedTrashType = spinnerTypes.getSelectedItem().toString();
       // LatLng newLocation = marker.getPosition();
        Log.d(TAG, "---saveData: " + selectedTrashType + " " + selectedLocation);

        for (TrashType type : trashTypeList) {
            if (type.typeName.equals(selectedTrashType)) {
                newContainer = new Container(Integer.valueOf(type.typeId), selectedLocation.longitude, selectedLocation.latitude);
                sendLocationToAPI(newContainer);
                return;
            }
        }
    }

    private void sendLocationToAPI(Container newContainer) {
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Call<ResponseBody> call = adminAPIService.addContainerOnLocation(jwt, newContainer);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddLocationActivity.this, "Location saved successfully", Toast.LENGTH_SHORT).show();
                    getContainersFromAPI(); // Refresh container data
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

        Call<List<TrashType>> call = userAPIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {
                if (response.code() == 400) {
                    Toast.makeText(AddLocationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    trashTypeList = new ArrayList<>(response.body());
                    trashTypeNames.clear();
                    for (TrashType type : trashTypeList) {
                        trashTypeNames.add(type.typeName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddLocationActivity.this, android.R.layout.simple_spinner_dropdown_item, trashTypeNames);
                    spinnerTypes.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(AddLocationActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getContainersFromAPI() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<Container>> call = adminAPIService.getAllContainerLocations(jwt);
        call.enqueue(new Callback<List<Container>>() {
            @Override
            public void onResponse(@NonNull Call<List<Container>> call, @NonNull Response<List<Container>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    allContainers = new ArrayList<>(response.body());
                    addMarkersForAllContainers(); // Update map with new markers
                } else {
                    Toast.makeText(AddLocationActivity.this, "Failed to fetch containers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Container>> call, @NonNull Throwable t) {
                Toast.makeText(AddLocationActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFilteredContainersFromAPI(){
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<Container>> call = userAPIService.getAllContainerLocations(jwt);
        call.enqueue(new Callback<List<Container>>() {
            @Override
            public void onResponse(@NonNull Call<List<Container>> call, @NonNull Response<List<Container>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allContainers = new ArrayList<>(response.body());
                    addMarkersForAllContainers(); // Update map with new markers
                } else {
                    Toast.makeText(AddLocationActivity.this, "Failed to load containers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Container>> call, @NonNull Throwable t) {
                Toast.makeText(AddLocationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addMarkersForAllContainers() {
        if (gMap != null && allContainers != null) {
            // Clear all markers except the location marker
            gMap.clear();

            // Re-add the location marker if it exists
            if (myLocationMarker != null) {
                myLocationMarker = gMap.addMarker(new MarkerOptions()
                        .position(myLocationMarker.getPosition())
                        .title("You are here"));
            }

            for (Container container : allContainers) {
                LatLng location = new LatLng(container.getLatitude(), container.getLongitude());
                gMap.addMarker(new MarkerOptions().position(location).title("Container ID: " + container.getContainerId()).draggable(true));
            }
        }
    }
}
