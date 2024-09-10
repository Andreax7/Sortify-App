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

    public AllUsersFAdapter(List<User> list, UserClickListener listener) {
        this.userList = list;
        this.listener = listener;
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

        User clickedUser = userList.get(position);

        Integer userStatus = clickedUser.getActive();
        holder.email.setText(clickedUser.getEmail());
        Integer role = clickedUser.getRole();

        holder.status.setText(userStatus == 1 ? "active" : "inactive");
        holder.role.setText(role == 1 ? "admin" : "user");

        holder.role.setOnClickListener(view -> {
            clickedUser.setRole(clickedUser.getRole() == 1 ? 0 : 1); // Toggle role
            holder.role.setText(clickedUser.getRole() == 1 ? "admin" : "user");

            //Log.d(TAG, "------Role changed: " + clickedUser.getUserId() + clickedUser.getEmail()+ " "+clickedUser.getRole() + " " + listener);
            if (listener != null) {
                listener.onUserRoleChanged(clickedUser);
            }
        });

        holder.status.setOnClickListener(view -> {
            clickedUser.setActive(clickedUser.getActive() == 1 ? 0 : 1); // Toggle status
            holder.status.setText(clickedUser.getActive() == 1 ? "active" : "inactive");
          //  Log.d(TAG, "----onClick in FRAGMENT ADAPTER: " + clickedUser.getUserId() + clickedUser.getEmail());
            if (listener != null) {
                listener.onUserStatusChanged(clickedUser);
            }
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }



}
