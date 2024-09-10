package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sortifyandroidapp.Adapters.ProductsAdapter;
import com.example.sortifyandroidapp.Adapters.TrashTypeAdapter;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Listeners.TypeListener;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
/**
 * This activity contains 2 recycler views with 2 buttons for adding Trash Type and Product
 *  Functionalities of updating TrashType or Product are implemented in the Adapters
 *  Button functionalities for adding new trash type and product are implemented in this activity
 *  Activity contain 2 api calls for getting trash types and products data from server and initialisation of recycler view and adapters
 *
 *  */

public class ExploreProductsActivity extends AppCompatActivity {

    private TrashTypeAdapter typeAdapter;
    private ProductsAdapter productsAdapter;
    private RecyclerView typeRecycler, productRecycler;
    private TypeListener typeListener;
    private FloatingActionButton addTypeBtn, addProductBtn, refreshBtn;
    private List<TrashType> allTypes;
    private List<Product> allProducts;
    private ActivityResultLauncher<Intent> editProductLauncher;

    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);

    ActivityResultLauncher<Intent> typeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                }
            }
    );
    ActivityResultLauncher<Intent> prodLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    //    Log.d(TAG, "--LAUNCHER DATA: " + data.getStringExtra("result"));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_products);

        allTypes = new ArrayList<>();
        typeAdapter = new TrashTypeAdapter(allTypes,typeListener);
        typeRecycler = findViewById(R.id.trashTypeRecycler);

        LinearLayoutManager manager = new LinearLayoutManager(ExploreProductsActivity.this);
        // setting empty layout manager for recycler view - to eliminate "no adapter attached error"
        typeRecycler.setLayoutManager(manager);
        typeRecycler.setAdapter(typeAdapter);
        getTypesFromDB();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(typeRecycler.getContext(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
        dividerItemDecoration.setDrawable(dividerDrawable);
        typeRecycler.addItemDecoration(dividerItemDecoration);


        allProducts = new ArrayList<>();
        productsAdapter = new ProductsAdapter(allProducts, allTypes, editProductLauncher );
        productRecycler = findViewById(R.id.productRecycler);

        LinearLayoutManager manager2 = new LinearLayoutManager(ExploreProductsActivity.this);
        productRecycler.setLayoutManager(manager2);
        productRecycler.setAdapter(productsAdapter);
        getProductsFromDB();


        refreshBtn = findViewById(R.id.refreshProductsBtn);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProductsFromDB();
            }
        });

        editProductLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        getProductsFromDB();
                    }
                }
        );


        productsAdapter = new ProductsAdapter(allProducts, allTypes, editProductLauncher);
        productRecycler = findViewById(R.id.productRecycler);
        productRecycler.setLayoutManager(new LinearLayoutManager(this));
        productRecycler.setAdapter(productsAdapter);


        typeListener = new TypeListener() {
            @Override
            public void click(int index) {
                Integer type = allTypes.get(index).typeId;
                Log.d(TAG, "----activity TYPE INDEX FOR FILTERING PRODUCTS " + type.toString() +" " + index);

                getFilteredProductsFromDB(String.valueOf(type));
                productsAdapter.filterProductsByType(type);

            }

        };

        typeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        allTypes = (List<TrashType>) data.getSerializableExtra("result");
                        // Setting adapter with new data to recycler view
                        typeAdapter = new TrashTypeAdapter(allTypes,typeListener);
                        typeRecycler.setAdapter(typeAdapter);
                    }
                }
        );

        addTypeBtn = findViewById(R.id.addTypeBtn);

        addTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewTypeIntent = new Intent(ExploreProductsActivity.this, AddNewTypeActivity.class);
                typeLauncher.launch(addNewTypeIntent);
                // bellow lines of code do not refresh current activity, so the launcher listener is created as above (DEPRICATED)
                //addNewTypeIntent.putExtra("adapter", (Serializable) allTypes);
                //startActivity(addNewTypeIntent);

            }
        });


        prodLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        getProductsFromDB();

                    }
                }
        );


        addProductBtn = findViewById(R.id.addProductBtn);

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewProductIntent = new Intent(ExploreProductsActivity.this, AddProductActivity.class);
                addNewProductIntent.putExtra("allTypes",(Serializable) allTypes);
                prodLauncher.launch(addNewProductIntent);
            }
        });

        // DELETE AND UPDATE TYPE BUTTONS --> IMPLEMENTED WITHIN TRASH TYPE ADAPTER
    }

    private void getTypesFromDB() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");
        // calling a method to get user data
        Call<List<TrashType>> call = userAPIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {

                if(response.code()==400){
                    Toast.makeText(ExploreProductsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                     allTypes = response.body();
                    typeAdapter = new TrashTypeAdapter(allTypes,typeListener);
                    typeRecycler.setAdapter(typeAdapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(ExploreProductsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProductsFromDB() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        // calling a method to get user data
        Call<List<Product>> call = userAPIService.getAllProducts(jwt);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                Log.d(TAG, "Raw JSON response: " + response.body().toString());
                if(response.code()==400 || response.code()==403 ){
                    Toast.makeText(ExploreProductsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    allProducts = response.body();
                    Log.d(TAG, "---onResponse: " + response);
                    if(allProducts == null ){
                        Toast.makeText(ExploreProductsActivity.this," no products yet to show", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        productsAdapter = new ProductsAdapter(allProducts, allTypes, editProductLauncher);
                        productRecycler.setAdapter(productsAdapter);
                    }

                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Log.d(TAG, "----onFailure: " +throwable.getMessage());
                Toast.makeText(ExploreProductsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void getFilteredProductsFromDB(String tid) {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<Product>> call = userAPIService.getProductsByType(jwt, tid);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                Log.d(TAG, "Raw JSON response: " + response.body().toString());
                if(response.code()==400 || response.code()==403 ){
                    Toast.makeText(ExploreProductsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    allProducts = response.body();
                    Log.d(TAG, "onResponse: " + allProducts);
                    if(allProducts == null ){
                        Toast.makeText(ExploreProductsActivity.this," no products yet to show", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        productsAdapter = new ProductsAdapter(allProducts, allTypes, editProductLauncher);
                        productRecycler.setAdapter(productsAdapter);
                    }

                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Log.d(TAG, "----onFailure: " +throwable.getMessage());
                Toast.makeText(ExploreProductsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}