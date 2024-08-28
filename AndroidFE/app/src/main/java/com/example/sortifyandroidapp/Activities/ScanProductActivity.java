package com.example.sortifyandroidapp.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import static android.content.ContentValues.TAG;


public class ScanProductActivity extends AppCompatActivity {

    private ActivityResultLauncher<ScanOptions> barLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        // Initialize the ActivityResultLauncher to handle the result of the scan
        barLauncher = registerForActivityResult(new ScanContract(), result -> {

            Log.d(TAG, "----HERE: " + result.getContents());
            // This block of code is executed when the barcode is successfully scanned
            if (result.getContents() != null) {

                Toast.makeText(ScanProductActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                // Show an alert dialog with the scanned data
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanProductActivity.this);
                builder.setTitle("Scan Result");
                builder.setMessage(result.getContents());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES); // Allow all barcode types

        barLauncher.launch(options);
    }

    private void scanCode() {


    }
}

    /** Check if this device has a camera */
    /*private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }*/

