package com.example.sortifyandroidapp.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Listeners.TypeListener;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.example.sortifyandroidapp.ViewHolders.TrashTypeViewHolder;

import java.util.List;

public class TypeUserAdapter extends RecyclerView.Adapter<TrashTypeViewHolder> {

    private List<TrashType> typeList;
    private TypeListener typeListener;

    public TypeUserAdapter(List<TrashType> typeList, TypeListener typeListener) {
        this.typeList = typeList;
        this.typeListener = typeListener;
    }


    @NonNull
    @Override
    public TrashTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the layout
        View trashTypeRecyclerView = inflater.inflate(R.layout.recycleview_typeuser_item, parent, false);
        TrashTypeViewHolder viewHolder = new TrashTypeViewHolder(trashTypeRecyclerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TrashTypeViewHolder holder, int position) {

        final int rowIndex = holder.getAdapterPosition();
        holder.trashType.setText(typeList.get(rowIndex).typeName);

        // filter products list by type
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Integer clickedIndex = typeList.get(rowIndex).typeId;
                Log.d(TAG, "----TYPE INDEX FOR FILTERING PRODUCTS " + String.valueOf(clickedIndex)); //String.valueOf(typeList.get(position).typeId));
                typeListener.click(rowIndex);

            }
        });



    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
