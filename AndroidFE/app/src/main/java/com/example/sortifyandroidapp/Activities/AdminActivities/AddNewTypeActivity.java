package com.example.sortifyandroidapp.Activities.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddNewTypeActivity extends AppCompatActivity {

    Button saveBtn;
    EditText typeName, typeInfo;
    TrashType newTrash;
    List<TrashType> newList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_new_type);

        //Intent previousActivityIntent = getIntent();

        typeName = findViewById(R.id.typeNameText);
        typeInfo = findViewById(R.id.typeInfoText);
        saveBtn = findViewById(R.id.saveTypeBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeInfoTxt = typeInfo.getText().toString();
                String typeNameTxt = typeName.getText().toString();
                if(!typeInfoTxt.isEmpty() & !typeNameTxt.isEmpty()){

                    newTrash = new TrashType(typeNameTxt,typeInfoTxt);
                    createNewTrashType(newTrash);
                }
            }

        });

    }

    private void createNewTrashType(TrashType newTrash) {

        // gets the connection and creates an instance for retrofit endpoint api class
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService APIService = retrofit.create(InterfaceAdminAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");
        //Toast.makeText(AddNewTypeActivity.this, newTrash.typeName, Toast.LENGTH_SHORT).show();

        // calling a method to signup and passing user class
        Call<List<TrashType>> call = APIService.addNewTrashType(jwt,newTrash);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(Call<List<TrashType>> call, Response<List<TrashType>> response) {
                if(response.code()==400 || response.code()==403 ){
                    Toast.makeText(AddNewTypeActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    newList = response.body();
                    Intent intent = new Intent(AddNewTypeActivity.this,ExploreProductsActivity.class);
                    intent.putExtra("result", (Serializable) newList);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<TrashType>> call, Throwable throwable) {
                Toast.makeText(AddNewTypeActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}