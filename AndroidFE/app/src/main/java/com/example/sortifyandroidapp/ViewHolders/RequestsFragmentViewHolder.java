package com.example.sortifyandroidapp.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.R;

public class RequestsFragmentViewHolder  extends RecyclerView.ViewHolder {

    public TextView formId, productInfo, seen;
    public View view;

    public RequestsFragmentViewHolder(@NonNull View itemView) {
        super(itemView);

        seen = itemView.findViewById(R.id.seenStatus);
        formId = itemView.findViewById(R.id.requestId);
        productInfo = itemView.findViewById(R.id.productInfo);

        view  = itemView;
    }
}
