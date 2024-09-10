package com.example.sortifyandroidapp.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.R;

public class RequestsFragmentViewHolder  extends RecyclerView.ViewHolder {

    public TextView formId, productName, seen, date;
    public View view;

    public RequestsFragmentViewHolder(@NonNull View itemView) {
        super(itemView);

        seen = itemView.findViewById(R.id.requestSeen);
        formId = itemView.findViewById(R.id.requestItemId);
        productName = itemView.findViewById(R.id.productNameTextView);
        date = itemView.findViewById(R.id.date);
        view  = itemView;
    }
}
