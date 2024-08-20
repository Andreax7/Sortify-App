package com.example.sortifyandroidapp.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.BreakIterator;

public class TrashTypeViewHolder extends RecyclerView.ViewHolder {

    public TextView trashType;
    public FloatingActionButton editTypeBtn, deleteTypeBtn;
    public View view;

    public TrashTypeViewHolder(View itemView){
        super(itemView);

        trashType = itemView.findViewById(R.id.trashTypeTextView);
        editTypeBtn = itemView.findViewById(R.id.editTypeBtn);
        deleteTypeBtn = itemView.findViewById(R.id.deleteTypeBtn);
        view  = itemView;
    }
}
