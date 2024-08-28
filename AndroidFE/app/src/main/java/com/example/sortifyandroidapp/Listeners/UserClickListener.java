package com.example.sortifyandroidapp.Listeners;

import com.example.sortifyandroidapp.Models.User;

public interface UserClickListener {
    void onUserRoleChanged(User clickedUser);

    void onUserStatusChanged(User clickedUser);
}
