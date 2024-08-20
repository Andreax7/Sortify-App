package com.example.sortifyandroidapp.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Listeners.TypeListener;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.example.sortifyandroidapp.ViewHolders.ProductsViewHolder;
import com.example.sortifyandroidapp.Windows.PopUpDeleteProductClass;
import com.example.sortifyandroidapp.Windows.PopUpDeleteTypeClass;
import com.example.sortifyandroidapp.Windows.PopUpUpdateProductClass;
import com.example.sortifyandroidapp.Windows.PopUpUpdateTypeClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsViewHolder>  {

    private List<Product> products;
    private List<TrashType> typeList;

    public ProductsAdapter(List<Product> products, List<TrashType> typeList) {

        this.products = products;
        this.typeList = typeList;
    }

    public void setProductList(List<Product> newProdList){
        this.products.clear();
        this.products = newProdList;
        notifyDataSetChanged();

        Log.d(TAG, "setProductList: " + products);
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
            // New window to update trash type and db call
            Product productObj = products.get(pIndex);
            Integer intProductId = products.get(pIndex).productId;

            PopUpUpdateProductClass popupUpdateClass = new PopUpUpdateProductClass();
            //PopupWindow popUp = popupUpdateClass.showPopupWindow(view,productObj);
            Toast.makeText(view.getContext(),productObj.productName + " "+  intProductId.toString(),Toast.LENGTH_SHORT).show();
          //  Button saveBtn = popupUpdateClass.getDataFromSaveBtn();
           /* saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String updatedName = popupUpdateClass.getUpdatedName();
                    String updatedInfo = popupUpdateClass.getUpdatedInfo();
                    if(!(updatedName==null) || !(updatedInfo==null)){
                        typeObj.typeName = updatedName;
                        typeObj.info = updatedInfo;
                        updateTypeInDB(view,typeObj,String.valueOf(intTypeId));
                        popUp.dismiss();
                    }
                }*/
            });


        // Intent updateProd = new Intent(ExploreProductsActivity.this, UpdateProductActivity.class);
        // updateProd.putExtra("ProductObj", (Serializable) product);
        // startActivity(updateType);

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
