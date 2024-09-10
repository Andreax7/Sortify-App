package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDropdown;
    Button saveBtn, captureButton;
    EditText barcode, productName, details;
    ImageView image;
    Integer selectedType;
    Product newProductObj;
    List<Product> newProductList;
    List<TrashType> typesList;
    List<String> typeNames = new ArrayList<>();
    HashMap<Integer, String> typeListMap = new HashMap<>();

    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private PreviewView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        image = findViewById(R.id.productImg);
        barcode = findViewById(R.id.barcodeField);
        productName = findViewById(R.id.productNameField);
        details = findViewById(R.id.productDetailsField);
        spinnerDropdown = findViewById(R.id.spinnerTypesList);
        captureButton = findViewById(R.id.loadPicBtn);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        // Initialize CameraX
        viewFinder = findViewById(R.id.viewFinder);
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera initialization failed.", e);
            }
        }, ContextCompat.getMainExecutor(this));

        // Get Types List from previous activity to populate Spinner
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Serializable types = extras.getSerializable("allTypes");
        typesList = (List<TrashType>) types;
        for (TrashType type : typesList) {
            typeNames.add(type.typeName);
            typeListMap.put(type.typeId, type.typeName);
        }
        Log.d("--SPINNER TYPES", String.valueOf(typeListMap));

        spinnerDropdown.setOnItemSelectedListener(this);
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeNames);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDropdown.setAdapter(dropdownAdapter);

        saveBtn = findViewById(R.id.saveProductBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String barcodeTxt = barcode.getText().toString();
                String detailsTxt = details.getText().toString();
                String decodedImg = encodeImage(image.getDrawable());
                String productNameTxt = productName.getText().toString();
                Integer tId = selectedType;

                if (!barcodeTxt.isEmpty() && tId != null && !productNameTxt.isEmpty() && !detailsTxt.isEmpty()) {
                    newProductObj = new Product(productNameTxt, barcodeTxt, decodedImg, tId, detailsTxt);
                    createNewProduct(newProductObj);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Required field not provided", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindCameraUseCases() {
        int rotation = viewFinder.getDisplay().getRotation();

        Preview preview = new Preview.Builder()
                .setTargetRotation(rotation)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        viewFinder.setRotation((float) 0.90);
        viewFinder.setRotation(rotation);

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_90)
                .build();

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
            Toast.makeText(this, "Camera initialization failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
    private void takePhoto() {
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();
                if (savedUri != null) {
                    Log.d(TAG, "Image saved successfully: " + savedUri.toString());
                } else {
                    Log.d(TAG, "Image saved successfully, but URI is null.");
                }

                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                    // Unbind the camera use cases after the photo is taken
                    if (cameraProvider != null) {
                        cameraProvider.unbindAll();
                    }
                } else {
                    Log.e(TAG, "Bitmap is null after decoding file.");
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Image capture failed: " + exception.getMessage(), exception);
            }
        });
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (cameraProvider != null) {
            bindCameraUseCases();
        }
    }

    private void createNewProduct(Product newProduct) {
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService APIService = retrofit.create(InterfaceAdminAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Log.d(TAG, "createNewProduct: " + newProduct.toString());

        newProduct.setImage(encodeImage(image.getDrawable()));

        Call<List<Product>> call = APIService.addNewProduct(jwt, newProduct);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                if(response.code()==400 || response.code()==403 ){
                    Log.e(TAG, "AddNewProductActivity response from server " + response.message());
                    Toast.makeText(AddProductActivity.this, " Failed to add product" , Toast.LENGTH_SHORT).show();
                }
                else{
                    newProductList = response.body();
                    Intent intent = new Intent(AddProductActivity.this, ExploreProductsActivity.class);
                    //intent.putExtra("result", (Serializable) newProductList);
                    setResult(RESULT_OK, intent);
                    Log.d(TAG, "Product added successfully. Finishing activity.");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedType = typesList.get(position).typeId;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private String encodeImage(Drawable drawable) {

        Bitmap originalBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originalBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        // Reducing dimensions
        int targetWidth = 300; // example width
        int targetHeight = (int) ((double) targetWidth / originalBitmap.getWidth() * originalBitmap.getHeight());
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);

        //Compressing
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}


