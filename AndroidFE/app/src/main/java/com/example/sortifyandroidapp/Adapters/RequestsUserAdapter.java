package com.example.sortifyandroidapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Models.Form;
import com.example.sortifyandroidapp.R;

import java.util.List;

public class RequestsUserAdapter extends RecyclerView.Adapter<RequestsUserAdapter.ViewHolder> {

    private List<Form> formList;

    // Constructor
    public RequestsUserAdapter(List<Form> formList) {
        this.formList = formList;
    }

    // ViewHolder class to hold references to the UI elements
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, formId, seen, date;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productNameTextView);
            formId = itemView.findViewById(R.id.requestItemId);
            seen = itemView.findViewById(R.id.requestSeen);
            date = itemView.findViewById(R.id.date);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the views
        Form form = formList.get(position);
        holder.title.setText(form.productName);
        holder.formId.setText(String.valueOf(form.formId));
        holder.seen.setText(String.valueOf(form.seen));
        holder.date.setText(form.date);
    }

    @Override
    public int getItemCount() {
        return formList.size();
    }

    // Method to update data in the adapter
    public void setForms(List<Form> forms) {
        this.formList = forms;
        notifyDataSetChanged();
    }
}
