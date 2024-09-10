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

    /*  constructor - called from ExploreProducts Activity  */
    public Button getConfirmDeleteBtn() {
        return confirmDeleteBtn;
    }


    public PopupWindow showPopupWindow(final View view, String id ) {

        productId = id;
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_delete_product, null);
        boolean focusable = true;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        confirmDeleteBtn = popupView.findViewById(R.id.deleteProductConfirmBtn);

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
