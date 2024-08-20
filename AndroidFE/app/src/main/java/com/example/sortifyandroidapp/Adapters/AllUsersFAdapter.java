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
import com.example.sortifyandroidapp.Models.User;
import com.example.sortifyandroidapp.R;
import com.example.sortifyandroidapp.ViewHolders.UserFragmentViewHolder;

import java.util.List;

public class AllUsersFAdapter extends RecyclerView.Adapter<UserFragmentViewHolder> {

    private List<User> userList ;
    private UserClickListener listener;

    public AllUsersFAdapter(List<User> list){
        this.userList = list;
    }

    @NonNull
    @Override
    public UserFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View UserRecyclerViewItem = inflater.inflate(R.layout.recyclerview_admin_user_item, parent, false);
        UserFragmentViewHolder viewHolder = new UserFragmentViewHolder(UserRecyclerViewItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserFragmentViewHolder holder, int position) {
        final int typeIndex = holder.getAdapterPosition();

        Integer userStatus = userList.get(typeIndex).getActive();
        holder.email.setText(userList.get(typeIndex).getEmail());
        if (userStatus == 1) {
            holder.status.setText("active");
        } else {
            holder.status.setText("inactive");
        }

        Log.d("ADAPTER ", String.valueOf(userList.get(typeIndex).getActive()));
        holder.view.setOnClickListener(view -> {
            // Send User data to new Activity
            Log.d(TAG, "onClick in FRAGMENT ADAPTER: "+ userList.get(typeIndex));
            //listener.onUserClick(userList.get(typeIndex));
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }



}
