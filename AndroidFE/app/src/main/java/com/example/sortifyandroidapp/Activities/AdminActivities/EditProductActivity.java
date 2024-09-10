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
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerDropdown;
    private Button saveBtn;
    private EditText barcode, productName, details;
    private ImageView image;
    private Integer selectedType;
    private Product selectedProduct;
    private List<TrashType> typesList;
    private List<String> typeNames = new ArrayList<>();
    private HashMap<Integer, String> typeListMap = new HashMap<>();
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private PreviewView viewFinder;
    private Integer selectedProductId;
    Integer currentPosition;
    List<Product> fromResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        image = findViewById(R.id.productImg);
        barcode = findViewById(R.id.barcodeField);
        productName = findViewById(R.id.productNameField);
        details = findViewById(R.id.productDetailsField);
        spinnerDropdown = findViewById(R.id.spinnerTypesList);
        Button captureButton = findViewById(R.id.loadPicBtn);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            selectedProductId = extras.getInt("product");
            typesList = (List<TrashType>) extras.getSerializable("allTypes");

            getProductByTypeFromDB();

        captureButton.setOnClickListener(view -> takePhoto());

        /** INITIALIZING CAMERA-X **/
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


            ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeNames);

            for (TrashType type : typesList) {
                typeNames.add(type.typeName);
                typeListMap.put(type.typeId, type.typeName);
                if(type.typeId == selectedProductId) {

                    currentPosition = dropdownAdapter.getPosition(String.valueOf(type.typeId));
                    spinnerDropdown.setSelection(currentPosition);
                }
            }

            spinnerDropdown.setAdapter(dropdownAdapter);
            spinnerDropdown.setOnItemSelectedListener(this);

        }

        // Save Button
        saveBtn = findViewById(R.id.saveProductBtn);
        saveBtn.setOnClickListener(v -> {
            String barcodeTxt = barcode.getText().toString();
            String detailsTxt = details.getText().toString();
            String decodedImg = encodeImage(image.getDrawable());
            String productNameTxt = productName.getText().toString();
            Integer tId = selectedType;

            if (!barcodeTxt.isEmpty() && tId != null && !productNameTxt.isEmpty() && !detailsTxt.isEmpty()) {
                selectedProduct.setProductName(productNameTxt);
                selectedProduct.setBarcode(barcodeTxt);
                selectedProduct.setImage(decodedImg);
                selectedProduct.setTypeId(tId);
                selectedProduct.setDetails(detailsTxt);
                selectedProduct.setProductId(selectedProduct.getProductId());

                updateProduct(selectedProduct);

            } else {
                Toast.makeText(EditProductActivity.this, "Required field not provided", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProductByTypeFromDB() {
        Retrofit retrofit = Connection.getClient();
        InterfaceUserAPIService APIService = retrofit.create(InterfaceUserAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");


        Call<List<Product>> call = APIService.getProductDataById(jwt, String.valueOf(selectedProductId));
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

               // Log.d(TAG, "--RESPONSE: " + response);
                if(response.code()==400 || response.code()==404 ){
                    Log.d(TAG, "AddNewProductActivity response from server " + response.message());
                    Toast.makeText(EditProductActivity.this, " Failed to add product" , Toast.LENGTH_SHORT).show();
                }
                else{
                        fromResponse = response.body();
                        selectedProduct = fromResponse.get(0);

                        productName.setText(selectedProduct.productName);
                        barcode.setText(selectedProduct.barcode);
                        details.setText(selectedProduct.details);

                        Bitmap bitmap = decodeImage(selectedProduct.image);
                        image.setImageBitmap(bitmap);
                       // Update the spinner selection based on product type cannot preform here because of null pointer exception

                    }
                    //Log.d(TAG, "-----Product " + response.body() );
                }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(EditProductActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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

    private void updateProduct(Product product) {
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService APIService = retrofit.create(InterfaceAdminAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Call<ResponseBody> call = APIService.updateProduct(jwt, String.valueOf(product.getProductId()), product);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 400 || response.code() == 403) {
                    Log.e(TAG, "UpdateProduct response from server " + response.message());
                    Toast.makeText(EditProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(EditProductActivity.this, ExploreProductsActivity.class);

                    setResult(RESULT_OK, intent);
                    Log.d(TAG, "-----Product updated successfully. Finishing activity.");
                    Toast.makeText(EditProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditProductActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedType = typesList.get(position).getTypeId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private String encodeImage(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        Bitmap resizedBitmap = resizeImage(bitmap);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap resizeImage(Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int maxSize = 1024; // Reducing resolution

        if (width > height) {
            float ratio = (float) width / maxSize;
            width = maxSize;
            height = (int) (height / ratio);
        } else if (height > width) {
            float ratio = (float) height / maxSize;
            height = maxSize;
            width = (int) (width / ratio);
        } else {
            width = maxSize;
            height = maxSize;
        }

        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }



    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
