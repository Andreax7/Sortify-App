package com.example.sortifyandroidapp.Activities.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;

import java.io.Serializable;

public class UpdateTypeActivity extends AppCompatActivity {

    Button saveChangesBtn;
    EditText typeInfo, typeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_new_type);

        saveChangesBtn = findViewById(R.id.saveTypeBtn);
        typeInfo = findViewById(R.id.typeInfoText);
        typeName = findViewById(R.id.typeNameText);

        Intent previousActivityIntent = getIntent();
        Serializable typeObj = previousActivityIntent.getSerializableExtra("trashTypeObj");
        TrashType passedType = (TrashType) typeObj;
        Log.d("-- EDIT TYPE --", typeObj.toString());
        typeName.setText(passedType.typeName);
        typeInfo.setText(passedType.info);

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save updated data and exit activity
            }
        });
    }
}