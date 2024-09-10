package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import static java.lang.Long.parseLong;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sortifyandroidapp.Activities.ContainersMapActivity;
import com.example.sortifyandroidapp.Activities.ScanProductActivity;
import com.example.sortifyandroidapp.Activities.StatisticsActivity;
import com.example.sortifyandroidapp.Activities.UserActivities.RecycleActivity;
import com.example.sortifyandroidapp.Activities.UserActivities.UserDataActivity;

import com.example.sortifyandroidapp.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/**
 * AdminProfile Activity contains admin fields for manipulating data such as
 * buttons to get/post or delete users, containers, products or types data
 * If token is not provided or its expired, the login Activity is returned
 *
 * **/


public class AdminProfileActivity extends AppCompatActivity {

    Button logoutBtn, myAccountBtn, usersAndRequestsBtn, mapBtn, scanBtn, productsBtn, recycleBtn, containersBtn, statisticsBtn ;
    TextView nameTextView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);


        logoutBtn = findViewById(R.id.logout2);
        myAccountBtn = findViewById(R.id.myAccountBtn2);
        usersAndRequestsBtn = findViewById(R.id.usersAndRequestsBtn);
        mapBtn = findViewById(R.id.mapBtn2);
        scanBtn = findViewById(R.id.scanBtn2);
        productsBtn = findViewById(R.id.productsBtn2);
        recycleBtn = findViewById(R.id.recycleBtn2);
        containersBtn = findViewById(R.id.containersBtn);
        statisticsBtn = findViewById(R.id.statsBtn);
        nameTextView = findViewById(R.id.nameTextView);


        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String res = sharedPref.getString("x-access-token", "");
        Log.d(TAG, "---OnCreate (AdminProfileActivity) ---- " + res);



        if(!res.isEmpty()){
            String jwt = res.substring(res.indexOf(" ") + 1);
                try {
                    JSONObject myUser = decodeToken(jwt);
                    if(isExpired(myUser)){
                        sharedPref.edit().clear().apply();
                        finishActivity(100);
                    }
                    nameTextView.setText(myUser.getString("firstName"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            /** ADMIN CRUD FUNCTIONALITIES **/

                logoutBtn.setOnClickListener(view -> {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.apply();
                    finish();
                });

                myAccountBtn.setOnClickListener(view -> {
                    Intent myProfileIntent = new Intent(AdminProfileActivity.this, UserDataActivity.class);
                    startActivity(myProfileIntent);
                });

                usersAndRequestsBtn.setOnClickListener(view -> {
                    Intent allUsersIntent = new Intent(AdminProfileActivity.this, AllUsersActivity.class);
                    startActivity(allUsersIntent);
                });


                productsBtn.setOnClickListener(view -> {
                    Intent productsIntent = new Intent(AdminProfileActivity.this, ExploreProductsActivity.class);
                    startActivity(productsIntent);
                });

                mapBtn.setOnClickListener(view -> {
                    Intent mapIntent = new Intent(AdminProfileActivity.this, ContainersMapActivity.class);
                    startActivity(mapIntent);
                });

                containersBtn.setOnClickListener(view -> {
                    Intent containersIntent = new Intent(AdminProfileActivity.this, AddLocationActivity.class);
                    startActivity(containersIntent);
                });


                scanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent scanIntent = new Intent(AdminProfileActivity.this, ScanProductActivity.class);
                        startActivity(scanIntent);
                    }
                });

                recycleBtn.setOnClickListener(view -> {
                    Intent recycleIntent = new Intent(AdminProfileActivity.this, RecycleActivity.class);
                    startActivity(recycleIntent);
                });


                statisticsBtn.setOnClickListener(view -> {
                    Intent statsIntent = new Intent(AdminProfileActivity.this, StatisticsActivity.class);
                    startActivity(statsIntent);
                });
        }

    }

    /** Check if this device has a camera */
    /*private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }*/

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String res = sharedPref.getString("x-access-token", "");

        if(!res.isEmpty()) {
            String jwt = res.substring(res.indexOf(" ") + 1);
            try {
                JSONObject myUser = decodeToken(jwt);
                if (isExpired(myUser)) {
                    sharedPref.edit().clear().apply();
                    finishActivity(100);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            finish();
        }

    /* END OF OnResume method */

    }

    /*
    * ADDITIONAL HELPERS METHODS
    * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected boolean isExpired(JSONObject jwtToken) {
        boolean valid = true;
        try {
            String exp = jwtToken.getString("exp");
            Date expires = Date.from(Instant.ofEpochSecond(parseLong(exp)));
            //  Log.d(TAG, "onResume validate Exp: " + exp+ " " + expires + " "+ new Date().before(expires));
            if (new Date().before(expires)) {
                valid = false;
            }
        } catch (JSONException e) {
            return valid;
        }
        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private JSONObject decodeToken(String token) throws JSONException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] tokenParts = token.split("\\.");
        String payload = new String(decoder.decode(tokenParts[1]));
        return new JSONObject(payload);
    }


}