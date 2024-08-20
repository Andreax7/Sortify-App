package com.example.sortifyandroidapp.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Listeners.UserClickListener;
import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.R;
import com.example.sortifyandroidapp.ViewHolders.RequestsFragmentViewHolder;

import java.util.List;


public class AllRequestsFAdapter extends RecyclerView.Adapter<RequestsFragmentViewHolder> {

    private List<Form> requestList ;
    private UserClickListener listener;


    public AllRequestsFAdapter(List<Form> reqList){
        this.requestList = reqList;
    }
    @NonNull
    @Override
    public RequestsFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View RequestRecyclerViewItem = inflater.inflate(R.layout.recyclerview_admin_request_item, parent, false);
        RequestsFragmentViewHolder viewHolder = new RequestsFragmentViewHolder(RequestRecyclerViewItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsFragmentViewHolder holder, int position) {

        final int requestIndex = holder.getAdapterPosition();
       // Integer requestStatus = userList.get(requestIndex).getActive();
       // holder.email.setText(userList.get(requestIndex).getEmail());
        //if (userStatus == 1) {
        //    holder.status.setText("active");
       // } else {
        //    holder.status.setText("inactive");
        //}

        Log.d("ADAPTER ", String.valueOf(requestIndex));
        holder.view.setOnClickListener(view -> {
            // Send User data to new Activity
            Log.d(TAG, "onClick in FRAGMENT ADAPTER requests: ");
            //listener.onUserClick(userList.get(typeIndex));
        });
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

}
