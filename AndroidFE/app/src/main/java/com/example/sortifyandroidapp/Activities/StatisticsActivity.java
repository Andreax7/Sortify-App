package com.example.sortifyandroidapp.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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

import com.example.sortifyandroidapp.Activities.UserActivities.RecycleActivity;
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

public class StatisticsActivity extends AppCompatActivity {


    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);
    Spinner containerSpinner, typeSpinner;
    TextView totalRecycledToday, averageRecycledUser, contPercentageText, typePercentageText;
    ProgressBar containerPercentage, typePercentage ;
    List<Recycled> totalRecycledData;
    List<Container> containersFromAPIArray;
    private ArrayList<String> trashTypeNames = new ArrayList<>();
    private List<TrashType> trashTypeList;
    private boolean isSpinnerInitial;
    private boolean isSpinnerInitial2;
    Integer activeContainersCount;
    private ArrayList<String> containerStreetArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        typeSpinner = findViewById(R.id.typeSpinner);
        containerSpinner = findViewById(R.id.containerSpinner);
        containerPercentage = findViewById(R.id.containerProg);
        typePercentage = findViewById(R.id.typeProg);
        totalRecycledToday = findViewById(R.id.totalRecycledToday);
        averageRecycledUser = findViewById(R.id.averageRecycledUser);
        contPercentageText = findViewById(R.id.containerPerField);
        typePercentageText = findViewById(R.id.typePerField);
        getAllLocations();
        getTypesFromDB();
        getRecycledData();
        getStatPerUser();

        containerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Container selectedCont = containersFromAPIArray.get(position);
                setContainerPercentage(selectedCont);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected trash type
                String selectedTypeName = trashTypeNames.get(position);
                TrashType selectedType = trashTypeList.get(position);
                Log.d(TAG, "----o-nItemSelected: "+ selectedType.typeId + " "+selectedType.typeName);
                updateRecyclingPercentage(selectedType.getTypeId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setContainerPercentage(Container cont) {
        float temp = 0;
        float totalRec = 0;
        for(Recycled rec : totalRecycledData){
            totalRec += rec.getQuantity();
            if(rec.getContainerId() == cont.getContainerId() && compareDates(rec.getDate())){
                temp+=rec.getQuantity();
            }
        }
        float calculation = (float) temp / totalRec;
        float percentage = calculation * 100;
        String resultText =String.valueOf(String.format("%.2f", percentage)+"%");
        Log.d(TAG, "---------setContainerPercentage: " + temp +" "+ totalRec +" ");
        contPercentageText.setText(resultText);
        containerPercentage.setProgress((int) percentage);
        //changeProgressBarColor(containerPercentage, (int) percentage);

    }


    private void updateRecyclingPercentage(Integer selectedType) {

        float totalQuantity = 0;
        float selectedQuantity = 0;
        ArrayList<Integer> containerIdsOfSelectedType = new ArrayList<>();

        for( Container contByType : containersFromAPIArray){
            if(contByType.getTypeId() == selectedType){
                containerIdsOfSelectedType.add(contByType.getContainerId());
            }
        }
        for (Recycled recycled : totalRecycledData) {
            totalQuantity += recycled.getQuantity();

            if (containerIdsOfSelectedType.contains(recycled.getContainerId())) {
                selectedQuantity += recycled.getQuantity();
            }
        }

        if (selectedQuantity > 0) {
            float percentage = (selectedQuantity / totalQuantity) * 100;
            String formattedPercentage = String.format("%.2f", percentage) + "%";

            typePercentage.setProgress(Math.round(percentage));
            typePercentageText.setText(formattedPercentage);
            //changeProgressBarColor(typePercentage, (int) percentage);
        } else {
            typePercentageText.setText("0%");
            typePercentage.setProgress(0);
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

                        // Counter to track when all street names have been retrieved
                        activeContainersCount = 0;
                        for (Container trash : containersFromAPIArray) {
                            if (trash.getActive() == 1) {
                                activeContainersCount++;
                            }
                        }

                        if (activeContainersCount == 0) {
                            Toast.makeText(StatisticsActivity.this, "No active containers available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final int[] fetchedStreetNamesCount = {0};

                        for (Container trash : containersFromAPIArray) {
                            if (trash.getActive() == 1) {
                                LatLng position = new LatLng(trash.getLatitude(), trash.getLongitude());

                                // Get the street name ASYNC
                                getStreetName(position, (streetName) -> {
                                    containerStreetArray.add(streetName);
                                    fetchedStreetNamesCount[0]++;

                                    if (fetchedStreetNamesCount[0] == activeContainersCount) {
                                        // Update spinner adapter once all street names are fetched
                                        runOnUiThread(() -> {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(StatisticsActivity.this, android.R.layout.simple_spinner_dropdown_item, containerStreetArray);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            containerSpinner.setAdapter(adapter);
                                        });
                                    }
                                });
                            }
                        }

                    } else {
                        Toast.makeText(StatisticsActivity.this, "No containers yet to show", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StatisticsActivity.this, "Failed to load container locations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Container>> call, Throwable throwable) {
                Toast.makeText(StatisticsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStreetName(LatLng position, StreetNameListener listener) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String streetName = "Unknown";
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

    private void getTypesFromDB() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<TrashType>> call = userAPIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    trashTypeList = response.body();
                    trashTypeNames.clear();
                    for (TrashType type : trashTypeList) {
                        trashTypeNames.add(type.typeName);
                    }

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(StatisticsActivity.this, android.R.layout.simple_spinner_dropdown_item, trashTypeNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        typeSpinner.setAdapter(adapter);
                    });
                } else {
                    Toast.makeText(StatisticsActivity.this, "Failed to load trash types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(StatisticsActivity.this, "An error occurred: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRecycledData() {

        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<Recycled>> call = userAPIService.getAllRecycledData(jwt);
        call.enqueue(new Callback<List<Recycled>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<List<Recycled>> call, @NonNull Response<List<Recycled>> response) {

                if(response.isSuccessful() && response.body() != null) {
                    totalRecycledData = new ArrayList<>();
                    totalRecycledData = response.body();

                    float recycledQuantityToday = 0;
                    float allRecycled = 0;
                    for (Recycled recycled : totalRecycledData) {
                        allRecycled += recycled.getQuantity();
                        if (compareDates(recycled.getDate())) {
                            recycledQuantityToday += recycled.getQuantity();
                        }
                    }

                    if (recycledQuantityToday == 0) {
                        totalRecycledToday.setText("0%");
                    } else {

                        float calculation = recycledQuantityToday / allRecycled;
                        float percentage = calculation * 100;
                        String resultText =String.valueOf(String.format("%.2f", percentage)+"%");

                        totalRecycledToday.setText(resultText);
                    }


                } else {
                    Toast.makeText(StatisticsActivity.this, "Failed to load recycled data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recycled>> call, @NonNull Throwable throwable) {
                Toast.makeText(StatisticsActivity.this, "An error occurred: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean compareDates(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate inputDate = LocalDate.parse(dateString, formatter);
            LocalDate today = LocalDate.now();
            return (inputDate.isEqual(today));

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void getStatPerUser(){
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<HashMap<String,Float>> call = userAPIService.getStatPerUser(jwt);
        call.enqueue(new Callback<HashMap<String,Float>>() {
            @Override
            public void onResponse(Call<HashMap<String,Float>> call, Response<HashMap<String,Float>> response) {
                HashMap<String,Float> res = response.body();
                averageRecycledUser.setText(res.get("total").toString() + " kg");
                Log.d(TAG, "--------onResponse: "+ res.toString());
            }

            @Override
            public void onFailure(Call<HashMap<String,Float>> call, Throwable t) {

            }
        });
    }


}