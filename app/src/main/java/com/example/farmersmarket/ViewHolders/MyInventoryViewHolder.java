package com.example.farmersmarket.ViewHolders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmersmarket.Models.Inventory;
import com.example.farmersmarket.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyInventoryViewHolder extends RecyclerView.ViewHolder {

    ImageView my_item_image;
    TextView my_item_label;
    TextView my_item_price;
    TextView my_item_strain;
    TextView my_item_cbd;
    TextView my_item_thc;
    TextView my_item_stock;
    TextView my_item_votes;
    Context context;
    public MyInventoryViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        my_item_image = itemView.findViewById(R.id.my_item_img);
        my_item_label = itemView.findViewById(R.id.my_item_label);
        my_item_price = itemView.findViewById(R.id.my_item_price);
        my_item_strain = itemView.findViewById(R.id.my_item_strain);
        my_item_cbd = itemView.findViewById(R.id.my_item_cbd);
        my_item_thc = itemView.findViewById(R.id.my_item_thc);
        my_item_stock = itemView.findViewById(R.id.my_item_stock);
        my_item_votes = itemView.findViewById(R.id.my_item_votes);
    }

    public void updateUi(Inventory inventory){
        my_item_label.setText(inventory.getLabel());
        my_item_price.setText("$"+inventory.getPricing());
        my_item_strain.setText(inventory.getStrain());
        my_item_cbd.setText("with "+inventory.getCbd()+"% CBD");
        my_item_thc.setText(", "+inventory.getThc()+"% THC");
        my_item_votes.setText("232");
        if (inventory.getInStock()){
            my_item_stock.setText("In-Stock");
            my_item_stock.setTextColor(ContextCompat.getColor(context, R.color.sell_fill));
        }
        else {
            my_item_stock.setText("Out-of-Stock");
            my_item_stock.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
    }


}
