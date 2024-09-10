package com.example.sortifyandroidapp.Activities.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sortifyandroidapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AllUsersActivity extends AppCompatActivity {

    Button allUsersBtnFragment, requestsBtnFragment;
    FloatingActionButton returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        allUsersBtnFragment = findViewById(R.id.myRequestsBtn);
        requestsBtnFragment = findViewById(R.id.sendRequestBtn);
        returnBtn = findViewById(R.id.backBtn2);

        allUsersBtnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment allUsersFragment = FragmentAllUsersList.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_frame, allUsersFragment, "all_users_fragment");
                transaction.commit();
            }
        });

        requestsBtnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment allRequestsFragment = FragmentAllUserRequests.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_frame, allRequestsFragment, "all_requests_fragment");
                transaction.commit();
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}