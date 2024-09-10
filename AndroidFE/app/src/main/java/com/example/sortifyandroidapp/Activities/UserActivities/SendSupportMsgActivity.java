package com.example.sortifyandroidapp.Activities.UserActivities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SendSupportMsgActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerDropdown;
    Button sendBtn, cancelBtn, captureButton;
    EditText barcode, productName, productDetails;
    ImageView productImage;
    Integer selectedType;
    Form newProductObj;
    List<Form> newFormList;
    List<TrashType> typesList;
    List<String> typeNames = new ArrayList<>();
    HashMap<Integer, String> typeListMap = new HashMap<>();

    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private PreviewView viewFinder;
    private static final int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        productImage = findViewById(R.id.productImg);
        barcode = findViewById(R.id.barcodeField);
        productName = findViewById(R.id.productNameField);
        productDetails = findViewById(R.id.productDetailsField);
        spinnerDropdown = findViewById(R.id.spinnerTypesList);
        captureButton = findViewById(R.id.loadPicBtn);
        cancelBtn = findViewById(R.id.cancelBtn2);

        cancelBtn.setOnClickListener(view -> finish());

        if (checkCameraPermission()) {
            initializeCamera();
        }

        captureButton.setOnClickListener(view -> {
            if (checkCameraPermission()) {
                takePhoto();
            }
        });


        // Get Types List from previous activity to populate Spinner
        getAllTTrashTypes();

        spinnerDropdown.setOnItemSelectedListener(this);
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeNames);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDropdown.setAdapter(dropdownAdapter);

        sendBtn = findViewById(R.id.sendReqestBtn2);
        sendBtn.setOnClickListener(view -> {
            String barcodeTxt = barcode.getText().toString();
            String detailsTxt = productDetails.getText().toString();
            String decodedImg = encodeImage(productImage.getDrawable());
            String productNameTxt = productName.getText().toString();
            Integer tId = selectedType;

            if (!barcodeTxt.isEmpty() && tId != null && !productNameTxt.isEmpty() && !detailsTxt.isEmpty()) {
                newProductObj = new Form(tId, productNameTxt, barcodeTxt, decodedImg, detailsTxt);
                sendRequest(newProductObj);
            } else {
                Toast.makeText(getApplicationContext(), "Required field not provided", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initializeCamera() {
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check the request code to ensure it's the camera request
        if (requestCode == CAMERA_REQUEST_CODE) {
            // If permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now bind the camera
                bindCameraUseCases();
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "Camera permission is required to capture photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bindCameraUseCases() {
        int rotation = viewFinder.getDisplay().getRotation();

        Preview preview = new Preview.Builder()
                .setTargetRotation(rotation)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(rotation)
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
                }
                else {
                    Log.d(TAG, "Image saved successfully, but URI is null.");
                }

                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                if (bitmap != null) {
                    productImage.setImageBitmap(bitmap);
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

    private void getAllTTrashTypes() {
        Retrofit retrofit = Connection.getClient();
        InterfaceUserAPIService APIService = retrofit.create(InterfaceUserAPIService.class);
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");
        // calling a method to get user data
        Call<List<TrashType>> call = APIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {
                if (response.code() == 400) {
                    Toast.makeText(SendSupportMsgActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    typesList = response.body();
                    for (TrashType type : typesList) {
                        typeNames.add(type.typeName);
                        typeListMap.put(type.typeId, type.typeName);
                    }
                    Log.d("--SPINNER TYPES", String.valueOf(typeListMap));

                    // Update spinner adapter with the new data
                    ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(SendSupportMsgActivity.this, android.R.layout.simple_spinner_dropdown_item, typeNames);
                    dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDropdown.setAdapter(dropdownAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(SendSupportMsgActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest(Form newProduct) {
        Retrofit retrofit = Connection.getClient();
        InterfaceUserAPIService APIService = retrofit.create(InterfaceUserAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        // Log the new product object
        Log.d(TAG, "----sendNewProduct: " + newProduct.productName);
        // Make sure to convert the image to a Base64 string if necessary
        newProduct.setProductImage(encodeImage(productImage.getDrawable()));

        Call<List<Form>> call = APIService.sendRequest(jwt, newProduct);
        call.enqueue(new Callback<List<Form>>() {
            @Override
            public void onResponse(@NonNull Call<List<Form>> call, @NonNull Response<List<Form>> response) {
                if (response.code() == 400 || response.code() == 403) {
                    Log.e(TAG, "-----SendRequestActivity response from server " + response.message());
                    Toast.makeText(SendSupportMsgActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                } else {
                    newFormList = response.body();
                    Log.d(TAG, newFormList.toString());
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Form>> call, @NonNull Throwable t) {
                Toast.makeText(SendSupportMsgActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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
        // Create a bitmap from the drawable
        Bitmap originalBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originalBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        // Resize the bitmap (reduce dimensions)
        int targetWidth = 300; // example width
        int targetHeight = (int) ((double) targetWidth / originalBitmap.getWidth() * originalBitmap.getHeight());
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);

        // Compress the resized bitmap to JPEG format with a quality of 40
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Encode the byte array to Base64 string
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

}