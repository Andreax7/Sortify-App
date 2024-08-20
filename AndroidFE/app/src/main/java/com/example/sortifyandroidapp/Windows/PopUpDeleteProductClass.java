package com.example.sortifyandroidapp.Windows;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.sortifyandroidapp.R;

public class PopUpDeleteProductClass {


    Button confirmDeleteBtn;
    String productId;

    /*  constructor - it is called from ExploreProducts Activity  */
    public Button getConfirmDeleteBtn() {
        return confirmDeleteBtn;
    }


    //PopupWindow display method
    public PopupWindow showPopupWindow(final View view, String id ) {

        productId = id;
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_delete_product, null);
        boolean focusable = true; // Make Inactive Items Outside Of PopupWindow
        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        //Initialize the elements of window
        confirmDeleteBtn = popupView.findViewById(R.id.deleteProductConfirmBtn);

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
