package com.example.farmersmarket.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.farmersmarket.Activities.MainActivity;
import com.example.farmersmarket.Adapters.InventoryAdapter;
import com.example.farmersmarket.Helpers.InventoryDatabaseTransaction;
import com.example.farmersmarket.Helpers.UserDatabaseTransaction;
import com.example.farmersmarket.Models.Inventory;
import com.example.farmersmarket.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public InventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    FloatingActionButton fab;
    RelativeLayout emptyInventoryLayout;
    RecyclerView inventoryRecycler;
    RelativeLayout addItemView;
    Spinner weightSpinner;
    Spinner strainSpinner;
    //input initializations
    EditText itemLabel;
    EditText cbd;
    EditText thc;
    EditText flavor;
    EditText price;
    Button sellBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);
        // set toolbar
        setToolbarSettings();

        // initializations
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        emptyInventoryLayout = (RelativeLayout)v.findViewById(R.id.empty_Inventory);
        inventoryRecycler = (RecyclerView)v.findViewById(R.id.inventory_recycler);
        addItemView = (RelativeLayout)v.findViewById(R.id.add_item_view);
        weightSpinner = (Spinner)v.findViewById(R.id.weight_spinner);
        strainSpinner = (Spinner) v.findViewById(R.id.strain_spinner);
        itemLabel = v.findViewById(R.id.item_label);
        cbd = v.findViewById(R.id.cbd);
        thc = v.findViewById(R.id.thc);
        flavor = v.findViewById(R.id.item_flavor);
        price = v.findViewById(R.id.item_price);
        sellBtn = v.findViewById(R.id.sell_btn);

        // set up recyclerview
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity()
                ,2, RecyclerView.VERTICAL, false);
        inventoryRecycler.setLayoutManager(layoutManager);
        emptyInventoryLayout.setVisibility(View.GONE);
        inventoryRecycler.setVisibility(View.VISIBLE);
        InventoryDatabaseTransaction idt = new InventoryDatabaseTransaction(getActivity());
        idt.getItemsFromDb(inventoryRecycler);



        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeInDataBase()){
                    Toast.makeText(getActivity(), "Item has been put up", Toast.LENGTH_SHORT).show();
                    clearFields();
                    ((MainActivity)getActivity()).attachInventoryFragment();
                }
                else {
                    Toast.makeText(getActivity(), "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setUpSpinners();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getToolbar().setTitle("Add item");
                fab.setVisibility(View.GONE);
                emptyInventoryLayout.setVisibility(View.GONE);
                inventoryRecycler.setVisibility(View.GONE);
                addItemView.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }
    private void setToolbarSettings(){
        ((MainActivity)getActivity()).getToolbar().setBackgroundColor(getResources().getColor(R.color.white));
        ((MainActivity)getActivity()).getToolbar().setTitle(R.string.inventory_title);
        ((MainActivity)getActivity()).getToolbar().setTitleTextColor(getResources().getColor(R.color.black));
    }
    private void setUpSpinners(){
        ArrayAdapter<CharSequence> weightSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.weight_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> strainSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.strain_array, android.R.layout.simple_spinner_item);

        weightSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        strainSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        weightSpinner.setAdapter(weightSpinnerAdapter);
        strainSpinner.setAdapter(strainSpinnerAdapter);

    }

    private boolean storeInDataBase(){
        if (itemLabel.getText().toString().equals("") || price.getText().toString().equals("")){
            return false;
        }
        Inventory inventoryItem = new Inventory();
        inventoryItem.setLabel(itemLabel.getText().toString());
        inventoryItem.setCbd(cbd.getText().toString());
        inventoryItem.setThc(thc.getText().toString());
        inventoryItem.setFlavor(flavor.getText().toString());
        inventoryItem.setPricing(price.getText().toString());
        inventoryItem.setStrain(strainSpinner.getSelectedItem().toString());
        inventoryItem.setWeightType(weightSpinner.getSelectedItem().toString());
        InventoryDatabaseTransaction inventoryDatabaseTransaction = new InventoryDatabaseTransaction(getActivity());
        inventoryDatabaseTransaction.storeInventoryItem(inventoryItem);
        Log.v("blow", "selected item in weight was "+weightSpinner.getSelectedItem().toString());
        return true;
    }

    private void clearFields(){
        itemLabel.setText("");
        cbd.setText("");
        thc.setText("");
        flavor.setText("");
        price.setText("");
    }
}
