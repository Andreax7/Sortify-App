package com.example.sortifyandroidapp.Activities.AdminActivities;

import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class SaveContainerLocationActivity extends FragmentActivity implements OnMapReadyCallback {


    private  GoogleMap gMap;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    Marker markers;

    private Spinner spinnerTypes;
    private ArrayList<String> trashTypeNames = new ArrayList<>(); // Initialize the ArrayList
    private ArrayList<TrashType> trashTypeList = new ArrayList<>(); // Initialize the ArrayList

    private String selectedTrashType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_container_locations);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}
