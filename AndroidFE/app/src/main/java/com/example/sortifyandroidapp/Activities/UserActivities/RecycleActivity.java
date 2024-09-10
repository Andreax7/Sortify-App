package com.example.sortifyandroidapp.Activities.UserActivities;

import static android.content.ContentValues.TAG;

import static java.lang.Float.parseFloat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Listeners.StreetNameListener;
import com.example.sortifyandroidapp.Models.Container;
import com.example.sortifyandroidapp.Models.Recycled;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecycleActivity extends AppCompatActivity {


    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);
    Button confirmBtn;
    Spinner spinnerLocation, typeSpinner;
    TextView trashPerTypeField, totalTrashField;
    EditText trashQuantity;
    ProgressBar progressBar;
    List<Recycled> totalRecycledData;
    private boolean isSpinnerInitialSelection = true;
    private boolean isSpinnerInitialSelection2 = true;
    private ArrayList<Container> containersFromAPIArray ;
    private ArrayList<String> containerStreetArray;
    private ArrayList<Recycled> myRecycledDataList;
    HashMap<String,Number> requestBody = new HashMap<>();
    private ArrayList<String> trashTypeNames = new ArrayList<>();
    private List<TrashType> trashTypeList = new ArrayList<>();
    Container selectedContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);

        spinnerLocation = findViewById(R.id.spinner_locations);
        confirmBtn = findViewById(R.id.confirmBtn);
        totalTrashField = findViewById(R.id.totalTrashField);
        trashPerTypeField = findViewById(R.id.containerPerField);
        typeSpinner = findViewById(R.id.trashTypeSpinner);
        trashQuantity = findViewById(R.id.trashQuantity);
        progressBar = findViewById(R.id.containerProg);
        getTotalStatsFromDB();
        getAllLocations();
        getTypesFromDB();
        setupSpinnerListener();

        // This button on click calls method to send data about thrown trash (when and how much)
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strQuantity = trashQuantity.getText().toString().trim();

                if (selectedContainer != null && !strQuantity.isEmpty()) {
                    try {
                        //Log.d(TAG, "-----onClick: (selectedContainerId)" + selectedContainer.getContainerId() +" TypeOfContainer:"+ selectedContainer.getTypeId());
                        Float convertedQuantity = parseFloat(strQuantity);

                        requestBody.put("containerId", selectedContainer.getContainerId());
                        requestBody.put("quantity", convertedQuantity);

                        if (selectedContainer.getContainerId() != null && !strQuantity.isEmpty()) {
                            throwMyTrash(requestBody);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Enter a valid number", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Select a container and enter a quantity", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getTotalStatsFromDB() {

            SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
            String jwt = sharedPref.getString("x-access-token", "");

            Call<List<Recycled>> call = userAPIService.getAllRecycledData(jwt);
            call.enqueue(new Callback<List<Recycled>>() {
                @Override
                public void onResponse(@NonNull Call<List<Recycled>> call, @NonNull Response<List<Recycled>> response) {

                    if(response.isSuccessful() && response.body() != null) {
                        totalRecycledData = new ArrayList<>();
                        totalRecycledData = response.body();
                        getMyStat();

                    } else {
                        Toast.makeText(RecycleActivity.this, "Failed to load trash types", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Recycled>> call, @NonNull Throwable throwable) {
                    Toast.makeText(RecycleActivity.this, "An error occurred: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void getMyStat() {

            SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
            String jwt = sharedPref.getString("x-access-token", "");

            Call<List<Recycled>> call2 = userAPIService.getMyRecycledData(jwt);
            call2.enqueue(new Callback<List<Recycled>>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call<List<Recycled>> call, Response<List<Recycled>> response) {

                    if(response.isSuccessful() && response.body() != null) {
                        myRecycledDataList = new ArrayList<>( response.body());

                        float allRecycledQuantityTemp = 0;
                        float tempMyRec = 0;

                        for (Recycled rec : totalRecycledData) {
                            allRecycledQuantityTemp += rec.getQuantity();
                        }
                        for (Recycled myRecycled : myRecycledDataList) {
                           if (compareDates(myRecycled.getDate())) {
                                    tempMyRec += myRecycled.getQuantity();
                                }
                            }

                        if (allRecycledQuantityTemp == 0) {
                            totalTrashField.setText("0%");
                        } else {

                            float calculation = tempMyRec / allRecycledQuantityTemp;
                            float percentage = calculation * 100;
                           // Log.d(TAG, "-----GET MY STATS onResponse: " + tempMyRec + " " + allRecycledQuantityTemp +" "+calculation );
                            String resultText =String.valueOf(String.format("%.2f", percentage)+"%");

                            totalTrashField.setText(resultText);
                        }

                    } else {

                        Toast.makeText(RecycleActivity.this, "Failed to load recycled data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Recycled>> call, Throwable t) {

                }
            });
        }



    private void setupSpinnerListener() {

        // Spinner with container locations (on location select)
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] parts = containerStreetArray.get(position).split(". ");
                Integer selectedContId = Integer.parseInt(parts[0].trim());

                for (Container container : containersFromAPIArray) {
                    if (container.getContainerId().equals(selectedContId)) {
                        selectedContainer = container;
                        break;
                    }
                }
                Log.d(TAG, "---------onItemSelected: " + selectedContId+" "+ "selectedContainerId: "+selectedContainer.getContainerId());

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Spinner with trashTypes
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSpinnerInitialSelection2) {
                    isSpinnerInitialSelection2 = false;
                    return;
                }

                TrashType selectedTrashType = trashTypeList.get(i);
               // Log.d(TAG, "----Selected Trash Type: " + selectedTrashType.typeName);
                getMyStatsPerType(selectedTrashType.typeId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getMyStatsPerType(Integer typeId) {

        float tempMyRecycledByType = 0; //
        for(Container cont : containersFromAPIArray){
            if(cont.getTypeId() == typeId && myRecycledDataList != null){
                for(Recycled rec: myRecycledDataList ){
                    if(cont.getContainerId() == rec.getContainerId() ){
                        tempMyRecycledByType += rec.getQuantity();
                    }
                }
            }
        }
        float tempTotalRecycledByType = 0;
        for(Recycled r : totalRecycledData){
            for(Container cont : containersFromAPIArray){
                if(cont.getTypeId() == typeId && cont.getContainerId() == r.getContainerId() ){
                    tempTotalRecycledByType+= r.getQuantity();
                }
            }
        }
        //Log.d(TAG, "-------getMYStatsPerType: " + tempMyRecycledByType + " " + tempTotalRecycledByType);
        if (tempTotalRecycledByType == 0) {
            trashPerTypeField.setText(String.format("%.2f", 0.0) + "%");
            progressBar.setProgress((int) 0);
        } else {
            float calculation = (float) tempMyRecycledByType / tempTotalRecycledByType;
            float percentage = calculation * 100;
            String resultText =String.valueOf(String.format("%.2f", percentage)+"%");
            trashPerTypeField.setText(resultText);
            progressBar.setProgress((int) percentage);
        }

    }

    // Gets the container table to set the street names in the dropdown
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
                        containerStreetArray = new ArrayList<>();
                        if (containersFromAPIArray.size() == 0) {
                            Toast.makeText(RecycleActivity.this, "No active containers available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (Container trash : containersFromAPIArray) {
                                LatLng position = new LatLng(trash.getLatitude(), trash.getLongitude());

                                // Get the street name ASYNC
                                getStreetName(position, (streetName) -> {
                                    for (TrashType type : trashTypeList) {
                                        if(trash.getTypeId() == type.typeId){
                                            containerStreetArray.add( trash.getContainerId() + ". " + type.typeName+": "+streetName);
                                        }
                                    }
                                        // Update spinner adapter once all street names are fetched
                                        runOnUiThread(() -> {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(RecycleActivity.this, android.R.layout.simple_spinner_dropdown_item, containerStreetArray);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinnerLocation.setAdapter(adapter);
                                        });
                                });
                        }

                    } else {
                        Toast.makeText(RecycleActivity.this, "No containers yet to show", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RecycleActivity.this, "Failed to load container locations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Container>> call, Throwable throwable) {
                Toast.makeText(RecycleActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method gets from container table latitude and longitude as parameters
     * using geocoder it sets street name using given location coordinates
     * **/
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
            String fullAddress = streetName + " " + streetNumber + " ";
            String finalStreetName = fullAddress;
            runOnUiThread(() -> listener.onStreetNameReceived(finalStreetName));
        }).start();
    }

    // Sends request to API
    private void throwMyTrash(HashMap requestBody) {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Log.d(TAG, "------throwMyTrash: " +requestBody);
        Call<ResponseBody> call = userAPIService.throwMyTrash(jwt, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.code() == 400) {
                    Toast.makeText(RecycleActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    if(response.code() == 201){
                        trashQuantity.setText("");
                        getTotalStatsFromDB();
                        //recreate();
                        Toast.makeText(RecycleActivity.this, "Your trash has been thrown successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Toast.makeText(RecycleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
/**
 * This method returns boolean true if date in recycled table row is today and seven days before
 *
 * **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean compareDates(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate inputDate = LocalDate.parse(dateString, formatter);
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysBefore = today.minusDays(7);

            return (inputDate.isEqual(today) || inputDate.isEqual(sevenDaysBefore) ||
                    (inputDate.isAfter(sevenDaysBefore) && inputDate.isBefore(today)));

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void getTypesFromDB() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<TrashType>> call = userAPIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    trashTypeList = response.body();
                    // Avoiding duplicates
                    trashTypeNames.clear();
                    for (TrashType type : trashTypeList) {
                        trashTypeNames.add(type.typeName);
                    }
                    // Updating the spinner on the UI thread
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(RecycleActivity.this, android.R.layout.simple_spinner_dropdown_item, trashTypeNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        typeSpinner.setAdapter(adapter);
                    });
                } else {
                    Toast.makeText(RecycleActivity.this, "Failed to load trash types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(RecycleActivity.this, "An error occurred: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
