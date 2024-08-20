package com.example.sortifyandroidapp.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sortifyandroidapp.Connection;
import com.example.sortifyandroidapp.Endpoints.InterfaceAdminAPIService;
import com.example.sortifyandroidapp.Windows.PopUpDeleteTypeClass;
import com.example.sortifyandroidapp.Windows.PopUpUpdateTypeClass;
import com.example.sortifyandroidapp.Listeners.TypeListener;
import com.example.sortifyandroidapp.Models.TrashType;
import com.example.sortifyandroidapp.R;
import com.example.sortifyandroidapp.ViewHolders.TrashTypeViewHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TrashTypeAdapter extends RecyclerView.Adapter<TrashTypeViewHolder> {

    private List<TrashType> typeList;
    private TypeListener typeListener;

    public TrashTypeAdapter(List<TrashType> typeList, TypeListener typeListener) {
        this.typeList = typeList;
        this.typeListener = typeListener;
    }

    public void setTypeList(List<TrashType> newtypeList){
        this.typeList.clear();
        this.typeList = newtypeList;
        notifyDataSetChanged();

        Log.d(TAG, "setTypeList: " + typeList);
    }

    @NonNull
    @Override
    public TrashTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the layout
        View trashTypeRecyclerView = inflater.inflate(R.layout.recyclerview_trashtype_item, parent, false);
        TrashTypeViewHolder viewHolder = new TrashTypeViewHolder(trashTypeRecyclerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TrashTypeViewHolder holder, int position) {

        final int typeIndex = holder.getAdapterPosition();
        holder.trashType.setText(typeList.get(position).typeName);

        /*
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                typeListener.click(typeIndex);
                Log.d("----TYPE INDEX FOR FILTERING PRODUCTS ", String.valueOf(typeIndex));
                // filter products list by type
            }
        });
       */


        holder.editTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // New window to update trash type and db call
                TrashType typeObj = typeList.get(typeIndex);
                Integer intTypeId = typeList.get(typeIndex).typeId;

                PopUpUpdateTypeClass popupUpdateClass = new PopUpUpdateTypeClass();
                PopupWindow popUp = popupUpdateClass.showPopupWindow(view,typeObj);

                Button saveBtn = popupUpdateClass.getDataFromSaveBtn();
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String updatedName = popupUpdateClass.getUpdatedName();
                        String updatedInfo = popupUpdateClass.getUpdatedInfo();
                        if(!(updatedName==null) || !(updatedInfo==null)){
                            typeObj.typeName = updatedName;
                            typeObj.info = updatedInfo;
                            updateTypeInDB(view,typeObj,String.valueOf(intTypeId));
                            popUp.dismiss();
                        }
                    }
                });
            }
        });


        holder.deleteTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // New window with confirmation and db call
                Integer intTypeId = typeList.get(typeIndex).typeId;

                PopUpDeleteTypeClass popUpDeleteClass = new PopUpDeleteTypeClass();
                PopupWindow popUp = popUpDeleteClass.showPopupWindow(view, String.valueOf(intTypeId));

                Button confirmBtn = popUpDeleteClass.getConfirmDeleteBtn();
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d(TAG, "--- onClickConfirm Delete: ");
                        deleteTypeFromDB(view,String.valueOf(intTypeId));
                        popUp.dismiss();
                    }
                });
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

    private void updateTypeInDB(View v, TrashType updated, String id){
        SharedPreferences sharedPref = v.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");
        // gets the connection and creates an instance for retrofit endpoint api class
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService adminAPIService = retrofit.create(InterfaceAdminAPIService.class);
        // calling API method for deleting trash type
        Call<List<TrashType>> call = adminAPIService.updateTrashType(jwt, id, updated);

        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(Call<List<TrashType>> call, Response<List<TrashType>> response) {
                if(response.code()==400 || response.code()==403){
                    Toast.makeText(v.getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    List<TrashType> newList = response.body();
                    setTypeList(newList);
                   // Log.d(TAG, "--UPDATE onResponse: " + newList);
                }
            }
            @Override
            public void onFailure(Call<List<TrashType>> call, Throwable throwable) {
                Toast.makeText(v.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteTypeFromDB(View view, String id) {

        SharedPreferences sharedPref = view.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        String jwt = sharedPref.getString("x-access-token", "");
        // gets the connection and creates an instance for retrofit endpoint api class
        Retrofit retrofit = Connection.getClient();
        InterfaceAdminAPIService adminAPIService = retrofit.create(InterfaceAdminAPIService.class);
        // calling API method for deleting trash type
        Call<List<TrashType>> call = adminAPIService.deleteTrashType(jwt,id);

        call.enqueue(new Callback<List<TrashType>>() {
            @Override
            public void onResponse(Call<List<TrashType>> call, Response<List<TrashType>> response) {
                if(response.code()==400 || response.code()==403){
                    Toast.makeText(view.getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                else{
                    List<TrashType> newList = response.body();
                    setTypeList(newList);
                    //Log.d(TAG, "onResponse: " + newList);
                }
            }
            @Override
            public void onFailure(Call<List<TrashType>> call, Throwable throwable) {
                Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
