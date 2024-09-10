package com.example.sortifyandroidapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.sortifyandroidapp.Activities.AdminActivities.ExploreProductsActivity;
import com.example.sortifyandroidapp.Adapters.TrashTypeAdapter;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScanProductActivity extends AppCompatActivity {

    private ActivityResultLauncher<ScanOptions> barLauncher;
    private String barcode;
    private List<TrashType> typeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeList = new ArrayList<>();
        getTrashTypeList();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startBarcodeScanner();
        }
    }

    private void getTrashTypeList() {
        Retrofit retrofit = Connection.getClient();
        InterfaceUserAPIService APIService = retrofit.create(InterfaceUserAPIService.class);
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<TrashType>> call = APIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {

                if(response.code()==400){
                    Toast.makeText(ScanProductActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    typeList = response.body();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(ScanProductActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startBarcodeScanner() {

     //   Log.d(TAG, "----START SCAN " + barcode);
        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            barcode = result.getContents();
            //Log.d(TAG, "----Scanned barcode: " + barcode);
            if (barcode != null) {
                getProductDataFromAPI(barcode);
            }
        });

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);

        barLauncher.launch(options);
    }

    private void getProductDataFromAPI(String barcode) {
        Retrofit retrofit = Connection.getClient();
        InterfaceUserAPIService APIService = retrofit.create(InterfaceUserAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Call<Product> call = APIService.getScannedProduct(jwt, barcode);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Product productData = response.body();
                    showProductDataDialog(productData);
                } else {

                    showProductNotFoundDialog();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(ScanProductActivity.this, "Failed to fetch product data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProductDataDialog(Product productData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanProductActivity.this);

        String typeName = "" ;
        for (TrashType typeObj : typeList) {
            if(typeObj.typeId == productData.typeId){
                typeName =typeObj.typeName;
            }
        }

        String newStr = String.format("product: %s %n trash type: %s ", productData.productName, typeName);
        builder.setTitle(newStr);
        builder.setMessage(productData.details);

        ImageView productImage = new ImageView(ScanProductActivity.this);
        Bitmap bitmap = decodeBase64ToBitmap(productData.image);

        if (bitmap != null) {
            productImage.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "Failed to decode image");
        }

        builder.setView(productImage);

        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }).show();
    }


    private void showProductNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanProductActivity.this);
        builder.setTitle("PRODUCT NOT IN DATABASE");
        builder.setMessage("Send barcode in the User Request");
        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }).show();
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {

        base64String = base64String.replaceAll("\\s", "");
        byte[] decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan barcodes", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}