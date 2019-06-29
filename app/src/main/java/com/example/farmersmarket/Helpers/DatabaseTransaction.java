package com.example.farmersmarket.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.farmersmarket.Fragments.MainFragment;
import com.example.farmersmarket.Models.User;
import com.example.farmersmarket.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

// NOTE: THIS WHOLE CLASS RUNS ASYNCRONOUSLY
public class DatabaseTransaction{
    FirebaseFirestore db;
    Activity activity;
    SharedPreferences sharedPreferences;
    List<User> userList = new ArrayList<User>();
    ClusterManager<User> mClusterManager;

    public DatabaseTransaction(Activity activity){
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
        sharedPreferences = this.activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void storeUserInDb(User user){
        String email = sharedPreferences.getString(activity.getString(R.string.email), null);
        String mission = sharedPreferences.getString(activity.getString(R.string.mission), null);
        if (email!=null){
            //email is also the document name
            user.setTitle(getFirstName());  // when user clicks a marker this title shows up
            db.collection(mission+"s").document(email).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }

    // this will be called when user signs out
    public void deleteUserFromDb(){
        String email = sharedPreferences.getString(activity.getString(R.string.email), null);
        Log.v("blow", "the email to be deleted is "+email);
        if (email!=null){
            db.collection("users").document(email).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.v("blow", "Successfully deleted from firestore");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("blow", "Failed to delete user from db");
                    e.printStackTrace();
                }
            });
        }
    }

    String getFirstName(){
        String firstName = "";
        int endIndex;
        String fullName = sharedPreferences.getString(activity.getString(R.string.userName), null);
        if (fullName != null){
            endIndex = fullName.lastIndexOf(" "); // last index because there could be a space in first name
            firstName = fullName.substring(0,endIndex);
        }
        return firstName;
    }
    // updateUser is an overloaded method
    public void updateUser(String field, double value){
        String email = sharedPreferences.getString(activity.getString(R.string.email), null);
        String mission = sharedPreferences.getString(activity.getString(R.string.mission), null);
        DocumentReference userRef = db.collection(mission+"s").document(email);
        userRef.update(field, value);
    }
    public void updateUser(String field, String value){
        String email = sharedPreferences.getString(activity.getString(R.string.email), null);
        String mission = sharedPreferences.getString(activity.getString(R.string.mission), null);
        DocumentReference userRef = db.collection(mission+"s").document(email);
        userRef.update(field, value);
    }


    public void getUsersWithinRange(final Location location, HashMap<String, Double> range, final GoogleMap map){
        Log.v("blow", "getUsersWithinRange triggered");
        String mission = sharedPreferences.getString(activity.getString(R.string.mission), null);
        Query usersRef;

        // if the user is a consumer, get producers and companyS
        // if the user is a producer or company, get consumers only
        if (mission!= null && mission.equals("consumer")){
            // search in producers
            usersRef = db.collection("producers").whereLessThan("lat", range.get("upperLat"))
                    .whereGreaterThan("lat", range.get("lowerLat"));
            usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e!=null){Log.e("blow", "there was an error getting producers");}
                    if (queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){
                        populateMap(location, queryDocumentSnapshots, map);
                    }
                }
            });

            // search in companys       (intentional typo)
            usersRef = db.collection("companys").whereLessThan("lat", range.get("upperLat"))
                    .whereGreaterThan("lat", range.get("lowerLat"));
            usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e!=null){Log.e("blow", "there was an error getting companys", e);}
                    if (queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){
                        populateMap(location, queryDocumentSnapshots, map);
                    }
                }
            });


        }

        else{
            Log.v("blow", "gonna query consumers");
            // query consumers only
            usersRef = db.collection("consumers").whereLessThan("lat", range.get("upperLat"))
                    .whereGreaterThan("lat", range.get("lowerLat"));
            usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e!=null){Log.e("blow", "there was an error getting consumers");}
                    if (queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){
                        populateMap(location, queryDocumentSnapshots, map);
                    }
                }
            });
        }
    }

    private void populateMap(final Location currentLoc, QuerySnapshot queryDocumentSnapshots, GoogleMap map){
        int fill_color;
        int stroke_color;
        String mission = sharedPreferences.getString(activity.getString(R.string.mission), null);
        if (mission!= null && mission.equals("consumer")){
            fill_color = R.color.sell_fill;
            stroke_color = R.color.sell_stroke;
        }
        else {
            fill_color = R.color.buy_fill;
            stroke_color = R.color.buy_stroke;
        }
        userList.clear(); // always start with a new list

        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            User user = documentSnapshot.toObject(User.class);
            LatLng customerLoc = new LatLng(user.getPosition().latitude, user.getPosition().longitude);
            float[] afloat ={0};    // the distance stores in here
            Location.distanceBetween(user.getPosition().latitude, user.getPosition().longitude,currentLoc.getLatitude(),
                    currentLoc.getLongitude(), afloat);
            if (afloat[0]<=4000){
                // if it is within '4000' of user location
                if (user.getMission().equals("company")){
                    userList.add(user);         //add user to the list of user
                    break;          // don't add circles for company
                }

                Log.v("blow", "distance is  "+ String.valueOf(afloat[0]));
                Log.v("blow", "gotten lat is  "+String.valueOf(user.getPosition().latitude));
                //exclude current user's position from the positions to draw a circle around
                if (!documentSnapshot.getId().equals(sharedPreferences.getString(activity.getString(R.string.email), null))){
                    Circle myLocCircle = map.addCircle(new CircleOptions().center(customerLoc)
                            .fillColor(ContextCompat.getColor(activity,fill_color))
                            .strokeColor(ContextCompat.getColor(activity, stroke_color))
                            .clickable(true)
                            .radius(70));
                    myLocCircle.setTag(documentSnapshot.getId());
                }
            }
            else {
                // if it is not in the distance
                Log.v("blow", "Not within distance "+String.valueOf(afloat[0]));
            }
        }
        setUpClustering(userList, map);
    }

    private void setUpClustering(List<User> users, GoogleMap map){
        mClusterManager = new ClusterManager<User>(activity, map);
        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        mClusterManager.addItems(userList);
        // set the click manager
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<User>() {
            @Override
            public boolean onClusterItemClick(User user) {
                Toast.makeText(activity, "marker clicked "+user.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mClusterManager.cluster();
        mClusterManager.setRenderer(new MarkerClusterRenderer(activity, map, mClusterManager));

    }
}
