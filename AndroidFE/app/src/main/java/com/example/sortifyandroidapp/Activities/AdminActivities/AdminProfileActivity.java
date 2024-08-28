package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import static java.lang.Long.parseLong;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sortifyandroidapp.Activities.ContainersMapActivity;
import com.example.sortifyandroidapp.Activities.ScanProductActivity;
import com.example.sortifyandroidapp.Activities.UserActivities.UserDataActivity;
import com.example.sortifyandroidapp.Adapters.ProductsAdapter;
import com.example.sortifyandroidapp.Adapters.TrashTypeAdapter;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * AdminProfile Activity contains admin fields for manipulating data such as
 * buttons to get/post or delete users, containers, products or types data
 * If token is not provided or its expired, the login Activity is returned
 *
 * **/


public class AdminProfileActivity extends AppCompatActivity {

    Button logoutBtn, myAccountBtn, usersAndRequestsBtn, mapBtn, scanBtn, productsBtn, recycleBtn, containersBtn, statisticsBtn ;
    TextView nameTextView;

    // gets the connection and creates an instance for retrofit endpoint api class

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

        // gets Access token for sending token in request
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




         /* Button for admin crud functionalities */

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
                     //   scanCode();

                    }
                });

        }
    /*
    * END OF BUTTON FUNCTIONNALITIES
    * END OF OnCreate method
    * */
    }

  /*  private void scanCode() {

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);

        barLauncher.launch(options);
    }

    //retrieving data after camera capture
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        Log.d(TAG, "----HERE: " + result.getContents());
        if(result.getContents() != null){
            AlertDialog.Builder bulider = new AlertDialog.Builder(AdminProfileActivity.this);
            bulider.setTitle("result");
            // get data from barcode
            bulider.setMessage(result.getContents());
            bulider.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
*/
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

    private JSONObject decodeToken(String token) throws JSONException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] tokenParts = token.split("\\.");
        String payload = new String(decoder.decode(tokenParts[1]));
        return new JSONObject(payload);
    }


}