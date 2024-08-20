package com.example.sortifyandroidapp.Windows;

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


    /*  constructor - it is called from ExploreProducts Activity  */
    public Button getDataFromSaveBtn() {
        return saveChangesBtn;
    }

    public String getUpdatedName(){
        return typeName.getText().toString();
    }

    public String getUpdatedInfo(){
        return typeInfo.getText().toString();
    }

    //PopupWindow display method
    public PopupWindow showPopupWindow(final View view, TrashType typeObj ) {
        passedView = view;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) passedView.getContext().getSystemService(passedView.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_new_type, null);
        boolean focusable = true; // Make Inactive Items Outside Of PopupWindow
        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of window
        saveChangesBtn = popupView.findViewById(R.id.saveTypeBtn);
        typeInfo = popupView.findViewById(R.id.typeInfoText);
        typeName = popupView.findViewById(R.id.typeNameText);

        Log.d("-- EDIT TYPE (on window open) --", typeObj.typeName);
        typeName.setText(typeObj.typeName);
        typeInfo.setText(typeObj.info);

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
        return popupWindow;
    }
}
