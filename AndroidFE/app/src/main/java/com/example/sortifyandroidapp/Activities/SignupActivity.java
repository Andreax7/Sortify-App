package com.example.sortifyandroidapp.Activities;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAPIAuthService;
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/********************************************************
 *********  ACTIVITY FOR REGISTERING ********************
 *******************************************************/

public class SignupActivity extends AppCompatActivity {

    FloatingActionButton backBtn;
    Button registerBtn;
    EditText emailField, firstNameField, lastNameField, passwordField, passRepeatField;
    TextView errorMsgEmail, errorMsgPass, errorMsgNames;
    ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_signup);

        // initializing buttons and fields
        backBtn = findViewById(R.id.backBtn);
        registerBtn = findViewById(R.id.registerBtn);
        emailField = findViewById(R.id.emailAddress);
        firstNameField = findViewById(R.id.firstName);
        lastNameField = findViewById(R.id.lastName);
        passwordField = findViewById(R.id.password);
        passRepeatField = findViewById(R.id.password2);
        errorMsgEmail = findViewById(R.id.errorMsgEmail);
        errorMsgPass = findViewById(R.id.errorMsgPass);
        errorMsgNames = findViewById(R.id.namesMsg);
        progressBar = findViewById(R.id.progressBar);

        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";

        // ***************************************************************************************
        //   FIELD CHECK - such as email verification, password matching and empty fields checks
        // ***************************************************************************************
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().matches(emailPattern) && editable.length() > 0){
                    errorMsgEmail.setText(" ");
                }
                else{
                   // Log.d("DEBUG"," --->" + Patterns.EMAIL_ADDRESS.matcher(email).matches());
                    errorMsgEmail.setText(" wrong email format ");
                }
            }
        });

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {

                String regex = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])"+ "(?=\\S+$).{8,25}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher patternMatcher = pattern.matcher(editable.toString());
               // write test result in debug: Log.d("MSG ", " "+ password.matches(regex));
                if(patternMatcher.matches())
                    errorMsgPass.setText("  ");
                else{
                    errorMsgPass.setText(" MIN 8 CHARACTERS WITH SMALL AND CAPITAL LETTERS ");
                }

            }
        });

        passRepeatField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String password = passwordField.getText().toString(); // takes the value again - first is empty
                Log.d(TAG, " MSG -> "+ password );
                if (!editable.toString().matches(password)) {
                    errorMsgPass.setText("Password not matching");
                }
                else{
                    errorMsgPass.setText(" ");
                }
            }
        });


        //**************************************************************************
        //                       END OF CHECK FIELDS
        //**************************************************************************

        //**************************************************************************
        //         REGISTER BUTTON - gets user data and sends to API

        registerBtn.setOnClickListener(view -> {
            String firstName = firstNameField.getText().toString();
            String lastName = lastNameField.getText().toString();
            String passwordr = passRepeatField.getText().toString();
            String email = emailField.getText().toString().trim();

            // below line is for displaying progress bar
            progressBar.setVisibility(View.VISIBLE);

            if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || passwordr.isEmpty()) {
                errorMsgNames.setText("EMPTY FIELDS NOT ALLOWED");
            }
            else{
                 User user = new User(email,firstName,lastName,passwordr);
                 sendData(user);
            }


        });

        // Back Button functionality -> Returns to Login Page
        backBtn.setOnClickListener( view -> {
            Intent intent = new Intent(SignupActivity.this,MainActivity.class);
            setResult(RESULT_OK,intent);
            finish();
        });


    }
    /* PASSING DATA */
    private void sendData(User user) {

        // gets the connection and creates an instance for retrofit endpoint api class
        Retrofit retrofit = Connection.getClient();
        InterfaceAPIAuthService APIService = retrofit.create(InterfaceAPIAuthService.class);

        // calling a method to signup and passing user class
        Call<String> call = APIService.signup(user);

        // executing method
        call.enqueue(new Callback<String>() {
            // getting response from API
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                // getting response from body and passing it to toast.
                String responseFromAPI = response.body();
                if(response.code()==400){
                    Toast.makeText(SignupActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, responseFromAPI, Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                //show error message in toast
                Log.d(TAG, "on FAIL  " + t);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignupActivity.this,""+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // private Boolean isEmpty(EditText etText){return etText.toString().isEmpty();}

}