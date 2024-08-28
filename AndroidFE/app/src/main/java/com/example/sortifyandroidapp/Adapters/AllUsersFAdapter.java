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
    private User clickedUser ;
    private UserClickListener listener; // Make sure this is correctly imported and used

    public AllUsersFAdapter(List<User> list, UserClickListener listener) { // Update constructor
        this.userList = list;
        this.listener = listener; // Initialize the listener
    }
    public AllUsersFAdapter(List<User> list) { // Update constructor
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
        Integer role = userList.get(typeIndex).getRole();

        holder.status.setText(userStatus == 1 ? "active" : "inactive");
        holder.role.setText(role == 1 ? "admin" : "user");

        clickedUser = userList.get(typeIndex);


        // Clicking on role changes user role
        holder.role.setOnClickListener(view -> {
            clickedUser.setRole(clickedUser.getRole() == 1 ? 0 : 1); // Toggle role
            holder.role.setText(clickedUser.getRole() == 1 ? "admin" : "user");

            // API CALL TO SWITCH ROLE
            if (listener != null) {
                listener.onUserRoleChanged(clickedUser);
            }
            Log.d(TAG, "Role changed: " + clickedUser.getRole());
        });

        // Clicking on status changes user status
        holder.status.setOnClickListener(view -> {
            clickedUser.setActive(clickedUser.getActive() == 1 ? 0 : 1); // Toggle status
            holder.status.setText(clickedUser.getActive() == 1 ? "active" : "inactive");

            if (listener != null) {
                listener.onUserStatusChanged(clickedUser);
            }
            Log.d(TAG, "Status changed: " + clickedUser.getActive());
        });

        holder.view.setOnClickListener(view -> {
            // Handle item click
            Log.d(TAG, "----onClick in FRAGMENT ADAPTER: " + clickedUser);
            // Optional: notify listener about item click if needed
            // if (listener != null) listener.onUserClick(clickedUser);
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }



}
