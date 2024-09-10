package com.example.sortifyandroidapp.Activities.AdminActivities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sortifyandroidapp.Activities.UserActivities.SendSupportMsgActivity;
import com.example.sortifyandroidapp.Activities.UserActivities.SupportActivity;
import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Endpoints.InterfaceUserAPIService;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FormDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String EXTRA_FORM = "selectedForm";
    private Form selectedForm;
    private Integer selectedType;
    private List<TrashType> typesList = new ArrayList<>();
    private List<String> typeNames = new ArrayList<>();
    private Spinner spinnerDropdown;
    private Button sendRequestBtn, cancelBtn;
    private EditText productName, barcode, productDetails;
    private ImageView image;
    private HashMap<Integer, String> typeListMap = new HashMap<>();
    private Integer currentPosition;
    private Product newProductObj;

    Retrofit retrofit = Connection.getClient();
    InterfaceUserAPIService userAPIService = retrofit.create(InterfaceUserAPIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_request);

        image = findViewById(R.id.productImg);
        barcode = findViewById(R.id.barcodeField);
        productName = findViewById(R.id.productNameField);
        productDetails = findViewById(R.id.productDetailsField);
        spinnerDropdown = findViewById(R.id.spinnerTypesList);
        sendRequestBtn = findViewById(R.id.sendReqestBtn2);
        cancelBtn = findViewById(R.id.cancelBtn2);


        spinnerDropdown.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        selectedForm = (Form) intent.getSerializableExtra(EXTRA_FORM);

        if (selectedForm != null) {

            productName.setText(selectedForm.productName);
            barcode.setText(selectedForm.barcode);
            productDetails.setText(selectedForm.productdetails);
            Bitmap convertedImg = decodeImageToBitmap(selectedForm.productImage);
            image.setImageBitmap(convertedImg);

            getTypesFromDB();
        } else {
            Toast.makeText(this, "No form data available.", Toast.LENGTH_SHORT).show();
        }


        sendRequestBtn.setOnClickListener(view -> {

            String barcodeTxt = barcode.getText().toString();
            String detailsTxt = productDetails.getText().toString();
            String decodedImg = encodeImage(image.getDrawable());
            String productNameTxt = productName.getText().toString();
            Integer tId = selectedType;

            if (!barcodeTxt.isEmpty() && tId != null && !productNameTxt.isEmpty() && !detailsTxt.isEmpty()) {
                newProductObj = new Product(productNameTxt, barcodeTxt, decodedImg, tId, detailsTxt);
                sendRequest(newProductObj);
            } else {
                Toast.makeText(getApplicationContext(), "Required field not provided", Toast.LENGTH_LONG).show();
            }
        });

        cancelBtn.setOnClickListener(view -> {
            finish();
        });
    }

    private Bitmap decodeImageToBitmap (String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    private String encodeImage(Drawable drawable) {

        Bitmap originalBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originalBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);


        int targetWidth = 300;
        int targetHeight = (int) ((double) targetWidth / originalBitmap.getWidth() * originalBitmap.getHeight());  // Reducing dimensions
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private void getTypesFromDB() {
        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");

        Call<List<TrashType>> call = userAPIService.getAllTypes(jwt);
        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(@NonNull Call<List<TrashType>> call, @NonNull Response<List<TrashType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    typesList = response.body();
                    for (TrashType type : typesList) {
                        typeNames.add(type.typeName);
                        typeListMap.put(type.typeId, type.typeName);
                    }

                    ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(FormDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, typeNames);
                    spinnerDropdown.setAdapter(dropdownAdapter);

                    if (selectedForm != null && typeListMap.containsKey(selectedForm.typeId)) {
                        currentPosition = dropdownAdapter.getPosition(typeListMap.get(selectedForm.typeId));
                        spinnerDropdown.setSelection(currentPosition);
                    }

                } else {
                    Toast.makeText(FormDetailActivity.this, "Failed to load trash types.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TrashType>> call, @NonNull Throwable throwable) {
                Toast.makeText(FormDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest(Product newProduct) {
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService adminAPIService = retrofit.create(InterfaceAdminAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        Call<List<Product>> call = adminAPIService.addNewProduct(jwt, newProduct);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                if(response.code()==400 || response.code()==403 ){
                    Toast.makeText(FormDetailActivity.this, " Failed to add product" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "Product added successfully. Finishing activity.");
                    changeUserFormStatus();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(FormDetailActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeUserFormStatus(){
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService adminAPIService = retrofit.create(InterfaceAdminAPIService.class);
        SharedPreferences sharedPrefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPrefs.getString("x-access-token", "");

        HashMap<String, Integer> body = new HashMap<>();
        body.put("seen", 1);

        Call<ResponseBody> call = adminAPIService.changeRequestStatus(jwt, selectedForm.formId, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==400 || response.code()==403 ){
                    Log.e(TAG, "-------- FormDetailActivity response from server " + response.message());
                    Toast.makeText(FormDetailActivity.this, " Failed to add product" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "Product added successfully. Finishing activity.");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FormDetailActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedTypeName = typeNames.get(position);
        for (Integer typeId : typeListMap.keySet()) {
            if (typeListMap.get(typeId).equals(selectedTypeName)) {
                selectedType = typeId;
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


}