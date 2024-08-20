package com.example.sortifyandroidapp.Activities.UserActivities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.Models.UserPassword;
import com.example.sortifyandroidapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class UserDataActivity extends AppCompatActivity {

    private User user;
    ImageView profilePic;
    EditText firstNameEdit, lastNameEdit, emailEdit, passChange, passChange2;
    TextView msgTxt;
    Button updateBtn, changePassBtn;

    // gets the connection and creates an instance for retrofit endpoint api class
    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        updateBtn = findViewById(R.id.updateBtn);
        changePassBtn = findViewById(R.id.changePass);
        profilePic = findViewById(R.id.profilePic);
        firstNameEdit = findViewById(R.id.firstNameEdit);
        lastNameEdit = findViewById(R.id.lastNameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        passChange = findViewById(R.id.passChange);
        passChange2 = findViewById(R.id.passChange2);
        msgTxt = findViewById(R.id.MsgTxtView);

        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        if (!jwt.isEmpty()){
            String token = jwt.split("\\ ")[1];
            JSONObject userFromJWT = decodeToken(token);
            Log.d(TAG, "-- USERDATA ACTIVITY- is token expired ---: " + isExpired(userFromJWT));
            if(isExpired(userFromJWT)){
                sharedPref.edit().clear().apply();
                finish();
            }
            // calling a method to get user data
            Call<User> call = userAPIService.getProfileData(jwt);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.code() == 400 || response.code() == 403) {
                        Toast.makeText(UserDataActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("GET USER ", String.valueOf(response.body()));
                        user = response.body();
                        firstNameEdit.setText(user.getFirstName());
                        lastNameEdit.setText(user.getLastName());
                        emailEdit.setText(user.getEmail());
                        profilePic.setImageBitmap(convertImg(user.getPicture())); // ---> DECODING IMG FROM STRING TO BITMAP
                    }
                }
                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                    Toast.makeText(UserDataActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // check the fields if they are not empty

                    user.setFirstName(firstNameEdit.getText().toString());
                    user.setLastName(lastNameEdit.getText().toString());
                    user.setEmail(emailEdit.getText().toString());
                    // decoding from Drawable to String
                    String decodedImg = decodeImg(profilePic.getDrawable());
                    user.setPicture(decodedImg);
                    //Log.d(TAG, "  --- IMAGE STRING: " + decodedImg);

                    if (checkFields(user.getFirstName(), user.getLastName()) && !((user.getEmail()).isEmpty())) {
                        Call<Object> call = userAPIService.updateProfileData(jwt, user);
                        call.enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                if (response.code() == 400) {
                                    Toast.makeText(UserDataActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                } else {
                                    Object tokenObj = response.body();
                                    String token = tokenObj.toString();
                                    String jwt = token.substring(token.indexOf("=") + 1, token.indexOf("}"));
                                    user.setToken(jwt);
                                    Log.d("----NEW TOKEN AFTER UPDATE: ", jwt);
                                    // Saving token into Shared Preferences
                                    SharedPreferences sPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit = sPrefs.edit();
                                    edit.putString("x-access-token", "Bearer " + jwt);
                                    edit.apply();

                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable throwable) {
                                Toast.makeText(UserDataActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        msgTxt.setText("Empty fields not allowed");
                    }
                }
            });

        passChange2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = passChange.getText().toString(); // takes the value again - first is empty
                if (!editable.toString().matches(password)) {
                    msgTxt.setText("Password not matching");
                } else {
                    msgTxt.setText(" ");
                }
            }
        });

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields(passChange.getText().toString(), passChange2.getText().toString())) {
                    String pass1 = passChange2.getText().toString();
                    String pass2 = passChange2.getText().toString();
                    if (pass1.matches(pass2)) {
                        UserPassword changedPass = new UserPassword(pass1);
                        Call<Object> call = userAPIService.updateUserPassword(jwt, changedPass);
                        call.enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                if (response.code() == 400) {
                                    Toast.makeText(UserDataActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                } else {
                                    Object tokenObj = response.body();
                                    String token = tokenObj.toString();
                                    String jwt = token.substring(token.indexOf("=") + 1, token.indexOf("}"));
                                    user.setToken(jwt);
                                    Log.d("----NEW TOKEN AFTER UPDATE: ", jwt);
                                    // Saving token into Shared Preferences
                                    SharedPreferences sPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit = sPrefs.edit();
                                    edit.putString("x-access-token", "Bearer " + jwt);
                                    edit.apply();

                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable throwable) {
                                Toast.makeText(UserDataActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    msgTxt.setText("Empty fields not allowed");
                }
            }
        });
    }else{
            finish();
        }


    }

    private JSONObject decodeToken(String token) {
        java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
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

    private Boolean isExpired(JSONObject jwtToken) {
        boolean valid = true;
        try {
            String exp = jwtToken.getString("exp");
            Date expires = Date.from(Instant.ofEpochSecond(Long.valueOf(exp)));
            //  Log.d(TAG, "UserData validate Exp: " + exp+ " " + expires + " "+ new Date().before(expires));
            if (new Date().before(expires)) {
                valid = false;
            }
        } catch (JSONException e) {
            return valid;
        }
        return valid;
    }

    private boolean checkFields(String field1, String field2) {
        if(field1.isEmpty() || field2.isEmpty())
            return false;
        else return true;
    }


    private String decodeImg(Drawable drawable){
        // Create a bitmap from the drawable
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Encode the byte array to a String using Base64 encoding
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap convertImg(String baseImg) {
        byte[] decodedString = Base64.decode(baseImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    }
