package com.example.sortifyandroidapp.Activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.sortifyandroidapp.Activities.AdminActivities.AddLocationActivity;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Endpoints.InterfaceIPEmulator;
import com.example.sortifyandroidapp.Endpoints.InterfaceIPMoto;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Listeners.StreetNameListener;
import com.example.sortifyandroidapp.MapInterface;
import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.ContainerLocation;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * THIS ACTIVITY SHOWS TO USERS AND ADMINS ALL THE CONTAINERS AS MARKERS NEARBY THE DEVICE LOCATION
 * FILTER CONTAINERS BY TYPE OF TRASH - CHOOSE TYPE AND MAP WILL SHOW ONLY CHOSEN CONTAINERS
 * ALL THE CONTAINER LOCATIONS ARE FETCHED FROM API
 * ***/
public class ContainersMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Spinner spinnerTypes;
    FloatingActionButton showAllMarkersBtn;
    private ArrayList<String> trashTypeNames = new ArrayList<>();
    private ArrayList<TrashType> trashTypeList = new ArrayList<>();
    private ArrayList<Container> containersFromAPIArray = new ArrayList<>();
    private Marker myLocationMarker;

    private boolean isSpinnerInitialSelection = true;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_container_locations);

        spinnerTypes = findViewById(R.id.spinner_types);
        showAllMarkersBtn = findViewById(R.id.showAllMarkersBtn);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        getTypesFromDB();
        getAllLocations();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupSpinnerListener();

        showAllMarkersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarkersToMap(containersFromAPIArray);
            }
        });
    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "-------onMapReady: ");
        gMap.setMyLocationEnabled(true);
        gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
           @Override
           public boolean onMyLocationButtonClick() {
               LatLng loc = new LatLng(43.52328, 16.45060);
               gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
               return true;
           }
        });

        getDeviceLocation(new MapInterface() {
            @Override
            public void onLocationAvailable(LatLng location) {

                LatLng myLocation = new LatLng(43.52328, 16.45060);
                myLocationMarker = gMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,  15.0f));
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                gMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
    }


    private void setupSpinnerListener() {
        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isSpinnerInitialSelection) {
                    addMarkersToMap(containersFromAPIArray);
                    isSpinnerInitialSelection = false;
                    return;
                }

                Integer selectedType = trashTypeList.get(position).getTypeId();

                List<Container> filteredContainers = new ArrayList<>();
                for (Container container : containersFromAPIArray) {
                    if (container.getTypeId().equals(selectedType)) {
                        filteredContainers.add(container);
                    }
                }

                if (filteredContainers.isEmpty()) {
                    addMarkersToMap(containersFromAPIArray);
                    Toast.makeText(ContainersMapActivity.this, "No containers of this type found.", Toast.LENGTH_SHORT).show();
                } else {
                    addMarkersToMap(filteredContainers);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                addMarkersToMap(containersFromAPIArray);
            }
        });
    }

    private void getDeviceLocation(final MapInterface callback) {
        if (ActivityCompat.checkSelfPermission(ContainersMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ContainersMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                   Log.d("---GETTING LOCATION", String.valueOf(location));

                    if (location != null) {
                        //HARCODED FIRST LOCATION DUE TO EMULATOR
                        LatLng res = new LatLng(43.52328, 16.45060);
                        callback.onLocationAvailable(res);  // Notify that the location is available

                    } else {
                        // double lat = location.getLatitude();
                        // double longi = location.getLongitude();
                        // latitude = String.valueOf(lat);
                        // longitude = String.valueOf(longi);

                        Toast.makeText(ContainersMapActivity.this, "Unable to find location.", Toast.LENGTH_SHORT).show();
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


    private void getAllLocations() {

        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<Container>> call = userAPIService.getAllContainerLocations(jwt);
        call.enqueue(new Callback<List<Container>>() {
            @Override
            public void onResponse(Call<List<Container>> call, Response<List<Container>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {

                        containersFromAPIArray = new ArrayList<>(response.body());
                        addMarkersToMap(containersFromAPIArray);
                    } else {
                        Toast.makeText(ContainersMapActivity.this, "No containers yet to show", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ContainersMapActivity.this, "Failed to load container locations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Container>> call, Throwable throwable) {
                Toast.makeText(ContainersMapActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkersToMap(List<Container> containerList) {
        if (gMap == null || containerList.isEmpty()) return;
        gMap.clear();

        if (myLocationMarker != null) {
            myLocationMarker = gMap.addMarker(new MarkerOptions()
                    .position(myLocationMarker.getPosition())
                    .title("You are here"));
        }

        for (Container container : containerList) {
            LatLng position = new LatLng(container.getLatitude(), container.getLongitude());
            Integer trashTypeId = container.getTypeId();

            for (TrashType type : trashTypeList) {
                if (type.typeId.equals(trashTypeId)) {
                    float markerColor = getMarkerColorByType(type.typeName.toUpperCase());

                    getStreetName(position, (streetName) -> {
                        gMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                                .title("Container "+ container.getContainerId() +": " + type.typeName  + ", " + streetName));
                    });
                }
            }
        }
    }

    private void getStreetName(LatLng position, StreetNameListener listener) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String streetName = "Unknown"; // Default value in case address is not found
            String streetNumber = "";
            try {
                List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    streetName = address.getThoroughfare();
                    streetNumber = address.getFeatureName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fullAddress = streetName + " " + streetNumber;
            String finalStreetName = fullAddress;
            runOnUiThread(() -> listener.onStreetNameReceived(finalStreetName));
        }).start();
    }

    private float getMarkerColorByType(String typeName) {
        switch (typeName) {
            case "MJESANI OTPAD":
             case "MJESOVITI OTPAD":
                case "KOMUNALNI OTPAD":
                 return BitmapDescriptorFactory.HUE_GREEN;
            case "PAPIR": return BitmapDescriptorFactory.HUE_BLUE;
            case "PLASTIKA": return BitmapDescriptorFactory.HUE_YELLOW;
            case "BIOOTPAD":
                case "ODJECA":
                    return BitmapDescriptorFactory.HUE_ORANGE;
            case "STAKLO": return BitmapDescriptorFactory.HUE_AZURE;
            case "ULJE":
                case "TETRAPAK": return BitmapDescriptorFactory.HUE_VIOLET;
            case "ELEKTRONIKA":
            default: return BitmapDescriptorFactory.HUE_RED;
        }
    }


    private void getTypesFromDB() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<TrashType>> call = userAPIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {

                if (response.code() == 400) {
                    Toast.makeText(ContainersMapActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    trashTypeList = (ArrayList<TrashType>) response.body();

                    for (TrashType type : trashTypeList) {
                        if (!type.typeName.isEmpty()) {
                            trashTypeNames.add(type.typeName);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ContainersMapActivity.this, android.R.layout.simple_spinner_dropdown_item, trashTypeNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTypes.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(ContainersMapActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

