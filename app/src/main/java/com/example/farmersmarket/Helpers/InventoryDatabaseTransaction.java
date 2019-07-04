package com.example.farmersmarket.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmersmarket.Adapters.InventoryAdapter;
import com.example.farmersmarket.Models.Inventory;
import com.example.farmersmarket.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InventoryDatabaseTransaction {

    FirebaseFirestore db;
    Activity activity;
    SharedPreferences sharedPreferences;
    List<Inventory> userList = new ArrayList<Inventory>();

    public InventoryDatabaseTransaction(Activity activity){
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
        sharedPreferences = this.activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void storeInventoryItem(Inventory inventoryItem){
        String email = sharedPreferences.getString(activity.getString(R.string.email), null);
        String mission = sharedPreferences.getString(activity.getString(R.string.mission), null);
        if (email!=null){
            //email is also the document name
            db.collection(mission+"s").document(email).collection("inventory")
                    .document(inventoryItem.getLabel()).set(inventoryItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.v("blow", "stored item in inventory");
                        }
                    });
        }
    }

    public void getItemsFromDb(final RecyclerView recyclerView){
        String email = sharedPreferences.getString(activity.getString(R.string.email), null);
        String mission = sharedPreferences.getString(activity.getString(R.string.mission), null);
        final List<Inventory> inventoryList = new ArrayList<>();
        if (mission!=null && email!=null){
            Task<QuerySnapshot> inventoryItems = db.collection(mission+"s").document(email).collection("inventory").get();
            inventoryItems.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot docs: queryDocumentSnapshots) {
                        Inventory item = docs.toObject(Inventory.class);
                        inventoryList.add(item);
                    }

                    // send the list to inventory adapter
                    // this commences population of recycler view
                    Log.v("blow", "size of items is "+inventoryList.size());
                    InventoryAdapter adapter = new InventoryAdapter(inventoryList, activity);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }


    }
}
