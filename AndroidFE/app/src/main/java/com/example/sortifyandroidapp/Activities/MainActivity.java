package com.example.sortifyandroidapp.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sortifyandroidapp.Activities.AdminActivities.AdminProfileActivity;
import com.example.sortifyandroidapp.Activities.UserActivities.UserProfileMainActivity;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAPIAuthService;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.R;

import org.json.JSONException;
import org.json.JSONObject;


import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Main Activity contains fields for logging in and buttons for logging in and registering
 * Every Activity in onCreate method has Shared Preferences field check if there is jwt token provided and if it is valid
 * If token is not provided or its expired, the login Activity is returned
 *
 * **/

public class MainActivity extends AppCompatActivity {

    Button loginBtn, registerBtn;
    EditText email, password;
    TextView errorMsgEmail;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        errorMsgEmail = findViewById(R.id.errorMsgEmail);
        progressBar = findViewById(R.id.progressBar);

        // check if user has token and if it is valid
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String res = sharedPref.getString("x-access-token", "");

        if (!res.isEmpty()) {
            String token = res.split("\\ ")[1];
            JSONObject userFromJWT = decodeToken(token);
          //  Log.d(TAG, "-- OnCreate (Main Activity)---: is token expired " + isExpired(userFromJWT));

            if (isExpired(userFromJWT)){
                sharedPref.edit().clear().apply();
            } else{
                try {
                    Intent intent;
                    if (userFromJWT.getString("role").equals("1")) {
                       intent = new Intent(MainActivity.this, AdminProfileActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, UserProfileMainActivity.class);
                   }
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
                if (editable.toString().matches(emailPattern) && editable.length() > 0) {
                    errorMsgEmail.setText(" ");
                } else {
                    errorMsgEmail.setText(" wrong email format ");
                }
            }
        });

        loginBtn.setOnClickListener(view -> {
            //1. Get user input and clean data
            progressBar.setVisibility(View.VISIBLE);
            if (checkData(email.getText().toString(), password.getText().toString())) {
                // 2. authorization to server (get headers and send to new intent)
                authorize(email.getText().toString(), password.getText().toString());
            } else {
                errorMsgEmail.setText(" check email format and password field");
            }
        });

        // register button leads to SignUpActivity for registering new users
        // in case they don't have an account
        registerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });


    }
/**************************************************************************************************
 *                  END OF OnCreate
 *
 ***/



    private boolean checkData(String email, String pass) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";

        if (email.isEmpty() || pass.isEmpty()) return false;
        return email.matches(emailPattern) && email.length() > 0;
    }


    private void authorize(String email, String pass) {
        // gets the connection and creates an instance for retrofit endpoint api class
        Retrofit retrofit = Connection.getClient();
        InterfaceAPIAuthService APIService = retrofit.create(InterfaceAPIAuthService.class);

        User user = new User(email, pass);

        Call<Object> call = APIService.login(user);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                 //Log.d(TAG, "---- onResponse: RESP --- " + response);
                if (response.code() == 400) {
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    Object tokenObj = response.body();
                    String token = tokenObj.toString();
                    String jwt = token.substring(token.indexOf("=") + 1, token.indexOf("}"));
                    user.setToken(jwt);

                    SharedPreferences sPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sPrefs.edit();
                    edit.putString("x-access-token", "Bearer " + jwt);
                    edit.apply();

                    try {
                        JSONObject userFromJWT = decodeToken(token);
                        user.setRole(userFromJWT.getInt("role"));

                        Intent intent;
                        if (user.getRole() == 1) {
                            intent = new Intent(MainActivity.this, AdminProfileActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, UserProfileMainActivity.class);
                        }
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable throwable) {
                //show error message in toast
                Log.d(TAG, "on FAIL  " + throwable);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private JSONObject decodeToken(String token) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] tokenParts = token.split("\\.");
        //String header = new String(decoder.decode(tokenParts[0]));
        String payload = new String(decoder.decode(tokenParts[1]));
        JSONObject userData = null;
        try {
            userData = new JSONObject(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userData;
    }

/**
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main_login);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        errorMsgEmail = findViewById(R.id.errorMsgEmail);
        progressBar = findViewById(R.id.progressBar);


        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String res = sharedPref.getString("x-access-token", "");
        Log.d(TAG, "-- ON RESUME:--- " + !res.isEmpty() +" "+ res);

        if (!res.isEmpty()) {
            String token = res.split("\\ ")[1];
            JSONObject userFromJWT = decodeToken(token);

            Log.d(TAG, "onResume: " + isExpired(userFromJWT));

            if (isExpired(userFromJWT)) {
                sharedPref.edit().clear().apply();
                Log.d(TAG, "onResume tkn: " + sharedPref.getString("x-access-token", ""));
                try {
                    authorize(userFromJWT.getString("email"), userFromJWT.getString("pass"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            //
              //Intent intent;
               // if (userFromJWT.getString("role").equals("1")) {
               //     intent = new Intent(MainActivity.this, AdminProfileActivity.class);
               // } else {
               //     intent = new Intent(MainActivity.this, UserProfileMainActivity.class);
                //}
              //  startActivity(intent);

            else {
                //  - Methods for buttons - login and register
                //        login button creates jwt and validates the user,
                //        register button adds new user in db - sends data to api
                //    - Email and password check fields

                email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
                        if (editable.toString().matches(emailPattern) && editable.length() > 0) {
                            errorMsgEmail.setText(" ");
                        } else {
                            errorMsgEmail.setText(" wrong email format ");
                        }
                    }
                });
                // Login button functionality
                loginBtn.setOnClickListener(view -> {
                    Log.d(TAG, "---CLICK --: ");
                    //1. Get user input and clean data
                    progressBar.setVisibility(View.VISIBLE);
                    if (checkData(email.getText().toString(), password.getText().toString())) {
                        // 2. authorization to server (get headers and send to new intent)
                        authorize(email.getText().toString(), password.getText().toString());
                    } else {
                        errorMsgEmail.setText(" check email format and password field");
                    }
                });

                // register button leads to SignUpActivity for registering new users
                // in case they don't have an account
                registerBtn.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                });

            }
        }
    }**/

    protected boolean isExpired(JSONObject jwtToken) {
        boolean valid = true;
        try {
            String exp = jwtToken.getString("exp");
            Date expires = Date.from(Instant.ofEpochSecond(Long.valueOf(exp)));
          //  Log.d(TAG, "onResume validate Exp: " + exp+ " " + expires + " "+ new Date().before(expires));
            if (new Date().before(expires)) {
                valid = false;
            }
        } catch (JSONException e) {
            return valid;
        }
        return valid;
    }
}