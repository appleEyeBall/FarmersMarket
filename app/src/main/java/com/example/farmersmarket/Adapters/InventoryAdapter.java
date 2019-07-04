package com.example.farmersmarket.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmersmarket.Models.Inventory;
import com.example.farmersmarket.Models.User;
import com.example.farmersmarket.R;
import com.example.farmersmarket.ViewHolders.MyInventoryViewHolder;

import java.util.List;


public class InventoryAdapter extends RecyclerView.Adapter<MyInventoryViewHolder> {

    List<Inventory> gottenItems;
    Context context;

    public InventoryAdapter(List<Inventory> gottenUsers, Context context) {
        this.gottenItems = gottenUsers;
        this.context = context;

    }

    @NonNull
    @Override
    public MyInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("blow", "oncreate view holder works");
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_item, parent, false);
        MyInventoryViewHolder vh = new MyInventoryViewHolder(cardView, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyInventoryViewHolder holder, int position) {
        // get the dataSet
        // put it in the holder's method
        Log.v("blow", "onBind works");
        holder.updateUi(gottenItems.get(position));

    }

    @Override
    public int getItemCount() {
        return gottenItems.size();
    }
}
