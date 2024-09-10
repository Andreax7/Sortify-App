package com.example.sortifyandroidapp.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Activities.UserActivities.ProductDetailDialogFragment;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.example.sortifyandroidapp.ViewHolders.ProductsViewHolder;
import com.example.sortifyandroidapp.Windows.PopUpDeleteProductClass;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ProductUserAdapter extends RecyclerView.Adapter<ProductsViewHolder>  {

    private List<Product> products;
    private List<Product> origList;
    private final List<TrashType> typeList;
    private List<Product> filteredProductList;



    // Constructor when initializing product list from DB
    public ProductUserAdapter(List<Product> products, List<TrashType> typeList) {

        this.products = products;
        this.typeList = typeList;

    }


    public void setFilteredList(List<Product> newProdList){
        this.products = newProdList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewProduct) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View productsRecyclerView = inflater.inflate(R.layout.recyclerview_productuser_item, parent, false);
        ProductsViewHolder viewHolder = new ProductsViewHolder(productsRecyclerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {

        int pIndex = holder.getAdapterPosition();
        String typeNameTemp = "";
        //  Log.d(TAG, "onBindViewHolder (Product): " + products.get(pIndex).productName);
        Bitmap convertedImg = convertImgForDisplay(products.get(position).image);
        holder.prodImg.setImageBitmap(convertedImg);
        holder.productName.setText(products.get(position).productName);

        for (TrashType typeObj : typeList) {
            if(typeObj.typeId == products.get(position).typeId){
                holder.typeName.setText(typeObj.typeName);
                typeNameTemp = typeObj.typeName;
            }
        }

        String finalTypeNameTemp = typeNameTemp;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Log.d(TAG, "--- On Product click : " + products.get(pIndex));
                Product clickedProduct = products.get(pIndex);

                // Create an instance of ProductDetailDialogFragment
                ProductDetailDialogFragment dialogFragment = ProductDetailDialogFragment.newInstance(clickedProduct, finalTypeNameTemp);

                // Show the dialog fragment
                dialogFragment.show(((AppCompatActivity)view.getContext()).getSupportFragmentManager(), "productDetailDialog");
            }
        });



    }

    private Bitmap convertImgForDisplay(String baseImg) {
        byte[] decodedString = Base64.decode(baseImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    @Override
    public void onAttachedToRecyclerView(
            @NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Method to filter products based on type
    public void filterProductsByType(int typeId) {
        filteredProductList = new ArrayList<>();
        origList = products;
        for (Product product : origList) {
            if (product.typeId == typeId) {
                filteredProductList.add(product);
            }
        }
        setFilteredList(filteredProductList);  // Update the adapter with the filtered list
    }


}
