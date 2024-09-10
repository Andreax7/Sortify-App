package com.example.sortifyandroidapp.Windows;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;

public class PopUpUpdateTypeClass {
    Button saveChangesBtn;
    EditText typeInfo, typeName;
    View passedView;


    /*  constructor - called from ExploreProducts Activity  */
    public Button getDataFromSaveBtn() {
        return saveChangesBtn;
    }

    public String getUpdatedName(){
        return typeName.getText().toString();
    }

    public String getUpdatedInfo(){
        return typeInfo.getText().toString();
    }

    public PopupWindow showPopupWindow(final View view, TrashType typeObj ) {
        passedView = view;

        LayoutInflater inflater = (LayoutInflater) passedView.getContext().getSystemService(passedView.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_new_type, null);
        boolean focusable = true;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        saveChangesBtn = popupView.findViewById(R.id.saveTypeBtn);
        typeInfo = popupView.findViewById(R.id.typeInfoText);
        typeName = popupView.findViewById(R.id.typeNameText);

        Log.d(TAG, "-- EDIT TYPE (on window open) --"+ typeObj.typeName);
        typeName.setText(typeObj.typeName);
        typeInfo.setText(typeObj.info);

         popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        return popupWindow;
    }
}
