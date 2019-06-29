package com.example.farmersmarket.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farmersmarket.Activities.MainActivity;
import com.example.farmersmarket.Helpers.DatabaseTransaction;
import com.example.farmersmarket.Models.User;
import com.example.farmersmarket.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int LOCATION_REQ_CODE = 100;

    private String mParam1;
    private String mParam2;


    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    View v;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseTransaction databaseTransaction;
    SharedPreferences sharedPreferences;
    Location mLocation;

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
        v = inflater.inflate(R.layout.fragment_maps, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.mapsFragment);
        v.setVisibility(View.GONE); // it will become visible when user accepts permission
        ((MainActivity) getActivity()).getToolbar().setVisibility(View.VISIBLE);    // was invisible in sign in screen

        // initializations
        databaseTransaction = new DatabaseTransaction(getActivity());   //initializing
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // remove inventory from navigation drawer if the user is a consumer
        ((MainActivity)getActivity()).decideAndRemoveInventory();


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAndRequestPermissions();
    }

   private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e("blow", "showing rationale");
                showRationaleDialog();
            } else {
                Log.e("blow", "gonna request permissions");
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQ_CODE);
            }
        } else {
            v.setVisibility(View.VISIBLE);
            Log.e("blow", "PERMISSION GRANTED");
            setMapUp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    v.setVisibility(View.VISIBLE);
                    setMapUp();
                } else {
                    Log.e("blow", "permission Denied");
                    v.setVisibility(View.GONE);
                }
        }
    }

    private void showRationaleDialog() {
        Log.e("blow", "Showing dialog");
        final MainActivity mainActivity = (MainActivity) getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Why we need your location")
                .setMessage(R.string.rationale_msg)
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
                    }
                })
                .show();
    }

    private void setMapUp() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("blow", "mapFragment is null.");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        // requires that permission be checked. but I checked already
        // code WILL not reach here until permission has been checked
        mMap.setMyLocationEnabled(true);
        getLocationUpdateInFireStore();
    }

   private void getLocationUpdateInFireStore(){
        // requires that permission be checked. but I checked already
        // code WILL not reach here until permission has been checked
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //store location in global variable in case we need to use it in another function
                //update user location in firestore
                mLocation = location;
                //convert Location to latLng... cuz user model only uses LatLng
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                databaseTransaction.updateUser("lat", location.getLatitude());
                databaseTransaction.updateUser("lon", location.getLongitude());
                //this databaseTransaction method also loads the circles on the map
                databaseTransaction.getUsersWithinRange(mLocation,getLocationRange(),mMap);

                // get the user's present location
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            }
        });
    }

   private HashMap<String, Double> getLocationRange(){
        // the hashmap stores the latitude bounds
        HashMap<String, Double> range = new HashMap<>();
        double latDiff = mLocation.getLatitude()/1000;
        // differences should be positive, not negative
        if (latDiff<0){
            latDiff*=-1;
        }

        range.put("upperLat", mLocation.getLatitude()+latDiff);
        range.put("lowerLat", mLocation.getLatitude()-latDiff);
        return range;

    }
}
