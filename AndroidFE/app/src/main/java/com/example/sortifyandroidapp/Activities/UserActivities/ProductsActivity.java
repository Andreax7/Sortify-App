package com.example.sortifyandroidapp.Activities.UserActivities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Activities.AdminActivities.AddNewTypeActivity;
import com.example.sortifyandroidapp.Activities.AdminActivities.AddProductActivity;
import com.example.sortifyandroidapp.Activities.AdminActivities.ExploreProductsActivity;
import com.example.sortifyandroidapp.Adapters.ProductUserAdapter;
import com.example.sortifyandroidapp.Adapters.ProductsAdapter;
import com.example.sortifyandroidapp.Adapters.TrashTypeAdapter;
import com.example.sortifyandroidapp.Adapters.TypeUserAdapter;
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

public class ProductsActivity extends AppCompatActivity {

    // creating a variable for recycler view,
    // array list, object type and adapter class for TrashType and Products
    private TypeUserAdapter typeUserAdapter;
    private ProductUserAdapter productUserAdapter;
    private RecyclerView typeRecycler, productRecycler;
    private TypeListener typeListener;
    private FloatingActionButton refreshBtn;
    private List<TrashType> allTypes;
    private List<Product> allProducts;

    // gets the connection and creates an instance for retrofit endpoint api class
    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);

    // define listener for adding new Trash Type/ Product
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
        setContentView(R.layout.activity_user_products);

        allTypes = new ArrayList<>();
        typeUserAdapter = new TypeUserAdapter(allTypes,typeListener);
        typeRecycler = findViewById(R.id.trashTypeRecycler);
        // below line is to set layout manager for recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(ProductsActivity.this);
        // setting empty layout manager for recycler view - to eliminate "no adapter attached error"
        typeRecycler.setLayoutManager(manager);
        typeRecycler.setAdapter(typeUserAdapter);
        getTypesFromDB();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(typeRecycler.getContext(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
        dividerItemDecoration.setDrawable(dividerDrawable);
        typeRecycler.addItemDecoration(dividerItemDecoration);

        allProducts = new ArrayList<>();
        productUserAdapter = new ProductUserAdapter(allProducts, allTypes ); // initializing products adapter to avoid no adapted attached error
        productRecycler = findViewById(R.id.productRecycler);
        // setting layout manager for recycler view.
        LinearLayoutManager manager2 = new LinearLayoutManager(ProductsActivity.this);
        productRecycler.setLayoutManager(manager2);
        productRecycler.setAdapter(productUserAdapter);
        getProductsFromDB();


        refreshBtn = findViewById(R.id.refreshProductsBtn2);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProductsFromDB();
            }
        });

        // Set up the adapter
        productUserAdapter = new ProductUserAdapter(allProducts, allTypes); // pass editProductLauncher here
        productRecycler = findViewById(R.id.productRecycler);
        productRecycler.setLayoutManager(new LinearLayoutManager(this));
        productRecycler.setAdapter(productUserAdapter);


        typeListener = new TypeListener() {
            @Override
            public void click(int index) {
                Integer type = allTypes.get(index).typeId;
                Log.d(TAG, "----activity TYPE INDEX FOR FILTERING PRODUCTS " + type.toString() +" " + index);

                getFilteredProductsFromDB(String.valueOf(type));
                productUserAdapter.filterProductsByType(type);

            }

        };


        // listener for updating Product Recycler after adding new Product
     /*   prodLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        getProductsFromDB();
                        // allProducts = (List<Product>) data.getSerializableExtra("result");
                        // below line we are running a loop to add data to adapter class.
                        //  productsAdapter = new ProductsAdapter(allProducts, allTypes);
                        //  productRecycler.setAdapter(productsAdapter);
                    }
                }
         );
      */


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
                    Toast.makeText(ProductsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    allTypes = response.body();
                    // below line we are running a loop to add data to adapter class.
                    typeUserAdapter = new TypeUserAdapter(allTypes,typeListener);
                    // below line is to set adapter to recycler view.
                    typeRecycler.setAdapter(typeUserAdapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(ProductsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
                //Log.d(TAG, "Raw JSON response: " + response.body().toString());
                if(response.code()==400 || response.code()==403 ){
                    Toast.makeText(ProductsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    allProducts = response.body();
                    if(allProducts == null ){
                        Toast.makeText(ProductsActivity.this," no products yet to show", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // below line we are running a loop to add data to adapter class.
                        productUserAdapter = new ProductUserAdapter(allProducts, allTypes);
                        // below line is to set adapter to recycler view.
                        productRecycler.setAdapter(productUserAdapter);
                    }

                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Log.d(TAG, "----onFailure: " +throwable.getMessage());
                Toast.makeText(ProductsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void getFilteredProductsFromDB(String tid) {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        // calling a method to get user data
        Call<List<Product>> call = userAPIService.getProductsByType(jwt, tid);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                Log.d(TAG, "Raw JSON response: " + response.body().toString());
                if(response.code()==400 || response.code()==403 ){
                    Toast.makeText(ProductsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    allProducts = response.body();
                    Log.d(TAG, "onResponse: " + allProducts);
                    if(allProducts == null ){
                        Toast.makeText(ProductsActivity.this," no products yet to show", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // below line we are running a loop to add data to adapter class.
                        productUserAdapter = new ProductUserAdapter(allProducts, allTypes);
                        // below line is to set adapter to recycler view.
                        productRecycler.setAdapter(productUserAdapter);
                    }

                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Log.d(TAG, "----onFailure: " +throwable.getMessage());
                Toast.makeText(ProductsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}
