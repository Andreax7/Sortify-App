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
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Activities.AdminActivities.EditProductActivity;
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


public class ProductsAdapter extends RecyclerView.Adapter<ProductsViewHolder>  {

    private List<Product> products;
    private List<Product> origList;
    private List<TrashType> typeList;
    private List<Product> filteredProductList;
    private ActivityResultLauncher<Intent> editProductLauncher;



    // Constructor when initializing product list from DB
    public ProductsAdapter(List<Product> products, List<TrashType> typeList,ActivityResultLauncher<Intent>  editProductLauncher) {

        this.products = products;
        this.typeList = typeList;
        this.editProductLauncher = editProductLauncher; // Assign it here
    }

    public void setProductList(List<Product> newProdList){
        products.clear();
        this.products = newProdList;

        notifyDataSetChanged();

        Log.d(TAG, "setProductList: " + products);
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
        View productsRecyclerView = inflater.inflate(R.layout.recyclerview_product_item, parent, false);
        ProductsViewHolder viewHolder = new ProductsViewHolder(productsRecyclerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {

        int pIndex = holder.getAdapterPosition();

      //  Log.d(TAG, "onBindViewHolder (Product): " + products.get(pIndex).productName);
        Bitmap convertedImg = convertImgForDisplay(products.get(position).image);
        holder.prodImg.setImageBitmap(convertedImg);
        holder.productName.setText(products.get(position).productName);

        for (TrashType typeObj : typeList) {
            if(typeObj.typeId == products.get(position).typeId){
                holder.typeName.setText(typeObj.typeName);
            }
        }


        holder.editProductBtn.setOnClickListener(view -> {

            // Prepare the intent to launch EditProductActivity
            Product productObj = products.get(pIndex);
            Integer intProductId = productObj.productId; // Gets id of the clicked product
            Intent editProductIntent = new Intent(holder.itemView.getContext(), EditProductActivity.class);

            editProductIntent.putExtra("product", productObj.productId);
            editProductIntent.putExtra("allTypes", (Serializable) typeList);
            // Use the launcher to start the activity
            editProductLauncher.launch(editProductIntent);


            });


        holder.deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // New window with confirmation and db call
                Integer intProductId = products.get(pIndex).productId;

                PopUpDeleteProductClass popUpDeleteClass = new PopUpDeleteProductClass();
                PopupWindow popUp = popUpDeleteClass.showPopupWindow(view, String.valueOf(intProductId));

                Button confirmBtn = popUpDeleteClass.getConfirmDeleteBtn();
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d(TAG, "--- onClickConfirm Delete: ");
                        deleteProductFromDB(view,String.valueOf(intProductId));
                        popUp.dismiss();
                    }
                });
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


    private void deleteProductFromDB(View view, String id) {

        SharedPreferences sharedPref = view.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");
        // gets the connection and creates an instance for retrofit endpoint api class
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService adminAPIService = retrofit.create(InterfaceAdminAPIService.class);
        // calling API method for deleting trash type
        Call<List<Product>> call = adminAPIService.deleteProduct(jwt,id);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.code()==400 || response.code()==403){
                    Toast.makeText(view.getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    List<Product> newList = response.body();

                    setProductList(newList);
                    //Log.d(TAG, "onResponse: " + newList);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Log.d(TAG, "-------onFailure: " + throwable.getMessage());
                Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
