package com.example.sortifyandroidapp.Activities.UserActivities;
import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sortifyandroidapp.Models.Product;
import com.example.sortifyandroidapp.R;

public class ProductDetailDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT = "product_key";
    private Product product;
    private String typeName;

    public static ProductDetailDialogFragment newInstance(Product product, String typeName) {
        ProductDetailDialogFragment fragment = new ProductDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);  // Use putParcelable if Product implements Parcelable
        args.putString("type_name", typeName);   // Pass typeName string
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Retrieve the product object from the arguments
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
            typeName = getArguments().getString("type_name");

        }

        // Initialize views and display product details
        TextView productNameTextView = view.findViewById(R.id.product_name);
        ImageView productImageView = view.findViewById(R.id.product_image);
        TextView detailsTextVew = view.findViewById(R.id.product_details);
        TextView typeNameTextView = view.findViewById(R.id.type_name);

        if (product != null) {
            productNameTextView.setText(product.productName);
            typeNameTextView.setText(typeName);
            detailsTextVew.setText(product.details);
            Bitmap productImage = convertImgForDisplay(product.image);
            Log.d(TAG, "onCreateView: " + typeName);
            productImageView.setImageBitmap(productImage);
        }

        return view;
    }

    // Assuming convertImgForDisplay is also needed here
    private Bitmap convertImgForDisplay(String image) {
        try {
            byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null; // Handle error
        }
    }
}
