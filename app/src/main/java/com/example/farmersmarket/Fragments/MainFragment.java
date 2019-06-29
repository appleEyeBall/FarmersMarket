package com.example.farmersmarket.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.farmersmarket.Activities.MainActivity;
import com.example.farmersmarket.Helpers.DatabaseTransaction;
import com.example.farmersmarket.Models.User;
import com.example.farmersmarket.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RC_SIGN_IN = 111;

    private String mParam1;
    private String mParam2;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    View v;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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
        v = inflater.inflate(R.layout.fragment_main, container, false);
        ((MainActivity)getActivity()).getToolbar().setVisibility(View.GONE);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null){
            Log.v("blow", "currentUser is Null");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.cann_img2)
                            .build(),
                    RC_SIGN_IN);
        }
        else {
            String theName =  auth.getCurrentUser().getDisplayName();
            Log.v("blow", "There is a current user "+ theName);


            ((MainActivity)getActivity()).switchToMapsFragment();
        }


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("blow", "On activity result");
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Log.v("blow", "logged in ");
                // Successfully signed in
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                if (email==null){email = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();}
                editor.putString(getString(R.string.userName), userName).commit();
                editor.putString(getString(R.string.email), email);
                if (sharedPreferences.getString(getString(R.string.mission), null) == null ) {
                    showMissionDialog();
                }
                else {
                    storeUserInFirestore();     // store the new user in firestore
                    ((MainActivity)getActivity()).switchToMapsFragment();
                }




                // ...
            } else {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut(final boolean signInAgain){
        editor.clear().commit();
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("blow", "signed out");
                        if (signInAgain){((MainActivity)getActivity()).switchToMainFragment();}
                    }
                });

    }
    private void deleteUser(){
        editor.clear().commit();
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("blow", "User deleted");
                    }
                });
    }

    private void showMissionDialog(){
        CharSequence first = "Consumer";
        CharSequence second = "Producer";
        CharSequence third = "Company";
        CharSequence[] mission = {first, second, third};
        final MainActivity mainActivity = (MainActivity) getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What best describes you? ")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (sharedPreferences.getString(getString(R.string.mission), null) == null ) {
                            deleteUser();
                            signOut(true);  // true value restarts fragment
                        }
                    }
                })
                .setItems(mission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                editor.putString(getString(R.string.mission), "consumer").commit();
                                ((MainActivity)getActivity()).decideAndRemoveInventory();
                                mainActivity.switchToMapsFragment();
                                break;
                            case 1:
                                editor.putString(getString(R.string.mission), "producer").commit();
                                ((MainActivity)getActivity()).decideAndRemoveInventory();
                                mainActivity.switchToMapsFragment();
                                break;
                            case 2:
                                editor.putString(getString(R.string.mission), "company").commit();
                                ((MainActivity)getActivity()).decideAndRemoveInventory();
                                mainActivity.switchToMapsFragment();
                                break;
                        }
                        storeUserInFirestore();
                    }
                }).show();
    }

   private void storeUserInFirestore(){
        //This function stores user in firestore after log in
        String userName = sharedPreferences.getString(getString(R.string.userName), null);
        User user = new User(userName,
                sharedPreferences.getString(getString(R.string.mission), null));

        DatabaseTransaction databaseTransaction = new DatabaseTransaction(getActivity());
        databaseTransaction.storeUserInDb(user);
    }


}
