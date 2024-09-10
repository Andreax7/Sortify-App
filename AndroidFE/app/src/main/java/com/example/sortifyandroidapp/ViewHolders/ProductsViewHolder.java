package com.example.sortifyandroidapp.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductsViewHolder extends RecyclerView.ViewHolder {
    public TextView productName, typeName;
    public ImageView prodImg;
    public FloatingActionButton editProductBtn, deleteProductBtn;
    public View view;

    public ProductsViewHolder(View itemView){
        super(itemView);

        prodImg = itemView.findViewById(R.id.productImage);
        productName = itemView.findViewById(R.id.productNameTextView);
        typeName = itemView.findViewById(R.id.date);
        editProductBtn = itemView.findViewById(R.id.editProductBtn);
        deleteProductBtn = itemView.findViewById(R.id.deleteProductBtn);
        view  = itemView;
    }
}
