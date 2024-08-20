package com.example.sortifyandroidapp.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.R;

public class UserFragmentViewHolder extends RecyclerView.ViewHolder {

    public TextView email, status;
    public View view;

    public UserFragmentViewHolder(@NonNull View itemView) {
        super(itemView);

        email = itemView.findViewById(R.id.userEmail);
        status = itemView.findViewById(R.id.userStatus);
        view  = itemView;
    }
}
