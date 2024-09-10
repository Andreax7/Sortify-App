package com.example.sortifyandroidapp.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Listeners.OnFormClickListener;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.R;
import com.example.sortifyandroidapp.ViewHolders.RequestsFragmentViewHolder;

import java.util.List;

public class AllRequestsFAdapter extends RecyclerView.Adapter<RequestsFragmentViewHolder> {

    public void setRequestList(List<Form> requestList) {
        this.requestList = requestList;
    }

    private List<Form> requestList;
    private OnFormClickListener listener;

    public AllRequestsFAdapter(List<Form> reqList, OnFormClickListener listener) {
        this.requestList = reqList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestsFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View RequestRecyclerViewItem = inflater.inflate(R.layout.recyclerview_request_item, parent, false);
        return new RequestsFragmentViewHolder(RequestRecyclerViewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsFragmentViewHolder holder, int position) {
        final Form currentForm = requestList.get(position);

        holder.seen.setText(currentForm.seen.toString());
        holder.productName.setText(currentForm.productName);
        holder.date.setText(currentForm.date);
        holder.formId.setText(currentForm.formId.toString());

        holder.view.setOnClickListener(view -> {
            Log.d(TAG, "onClick in FRAGMENT ADAPTER requests: " + currentForm);
            listener.onFormClick(currentForm);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}