package com.example.sortifyandroidapp.Activities.UserActivities;
import static android.content.ContentValues.TAG;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sortifyandroidapp.Activities.ContainersMapActivity;
import com.example.sortifyandroidapp.Activities.ScanProductActivity;
import com.example.sortifyandroidapp.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Base64;


/* ***************************************************************************************************************
    This is the Main User Page/Activity that shows after login.
    It contains buttons that lead to other activities.
    Jwt token contains role and is saved into Shared Preferences,
     so when the user exits app without logout and runs the app again, it opens this activity if the role is user.
   ***************************************************************************************************************
*/
public class UserProfileMainActivity extends AppCompatActivity {

    Button logoutBtn, myAccountBtn, supportBtn, mapBtn, scanBtn, productsBtn, recycleBtn, statisticsBtn ;
    TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        logoutBtn = findViewById(R.id.logout);
        myAccountBtn = findViewById(R.id.myAccountBtn);
        supportBtn = findViewById(R.id.supportBtn);
        mapBtn = findViewById(R.id.mapBtn);
        scanBtn = findViewById(R.id.scanBtn);
        productsBtn = findViewById(R.id.productsBtn);
        recycleBtn = findViewById(R.id.recycleBtn);
        statisticsBtn = findViewById(R.id.statisticsBtn);
        nameTextView = findViewById(R.id.nameTextView);

        // gets Access token
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String res = sharedPref.getString("x-access-token", "");
        String jwt = res.substring(res.indexOf(" ") + 1);

        try {
            JSONObject myUser = decodeToken(jwt);
            nameTextView.setText(myUser.getString("firstName"));
            Log.d(TAG, "---SHARED and decoded ---- " +jwt + myUser.getString("firstName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
                finish();
            }
        });

        myAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myProfileIntent = new Intent(UserProfileMainActivity.this, UserDataActivity.class);
                startActivity(myProfileIntent);
            }
        });

        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent supportIntent = new Intent(UserProfileMainActivity.this, SupportActivity.class);
                startActivity(supportIntent);
            }
        });


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(UserProfileMainActivity.this, ContainersMapActivity.class);
                startActivity(mapIntent);
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        productsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productIntent = new Intent(UserProfileMainActivity.this, ProductsActivity.class);
                startActivity(productIntent);
            }
        });

        recycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recycleIntent = new Intent(UserProfileMainActivity.this, RecycleActivity.class);
                startActivity(recycleIntent);
            }
        });

        statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statisticsIntent = new Intent(UserProfileMainActivity.this, ScanProductActivity.class);
                startActivity(statisticsIntent);
            }
        });
    }

    private JSONObject decodeToken(String token) throws JSONException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] tokenParts = token.split("\\.");
        //String header = new String(decoder.decode(tokenParts[0]));
        String payload = new String(decoder.decode(tokenParts[1]));
        JSONObject userData = new JSONObject(payload);
        return userData;
    }
}
