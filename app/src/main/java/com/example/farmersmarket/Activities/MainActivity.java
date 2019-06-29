package com.example.farmersmarket.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.farmersmarket.Fragments.InventoryFragment;
import com.example.farmersmarket.Fragments.MainFragment;
import com.example.farmersmarket.Fragments.MapsFragment;
import com.example.farmersmarket.Helpers.DatabaseTransaction;
import com.example.farmersmarket.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    ConstraintLayout statusColumn;

    public NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // sets the hamburger icon in the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Fragment mainFragment = fragmentManager.findFragmentById(R.id.mainContainer);
        //MainFragment is the first fragments that loads. it contains the sign in screen
        if(mainFragment == null){
            mainFragment = MainFragment.newInstance("a", "b"); // just random arguments
            fragmentManager.beginTransaction().add(R.id.mainContainer, mainFragment).commit();
        }
        statusColumn = (ConstraintLayout)findViewById(R.id.statusColumn);
        setUpNavDrawer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // this helps the hamburger icon pull out the nav drawer
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Bellow are methods that will be called from other fragments
    public void switchToMapsFragment(){
        fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.mainContainer);
        if (existingFragment == null){
            MapsFragment mapsFragment = MapsFragment.newInstance("a","b");
            fragmentManager.beginTransaction().add(R.id.mainContainer, mapsFragment).commit();
        }
        else {
            MapsFragment mapsFragment = MapsFragment.newInstance("a","b");
            fragmentManager.beginTransaction().replace(R.id.mainContainer, mapsFragment).commit();
        }
    }
    public void switchToMainFragment(){
        fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.mainContainer);
        if (existingFragment == null){
            MainFragment mainFragment = MainFragment.newInstance("a","b");
            fragmentManager.beginTransaction().add(R.id.mainContainer, mainFragment).commit();
        }
        else {
            MainFragment mainFragment = MainFragment.newInstance("a","b");
            fragmentManager.beginTransaction().replace(R.id.mainContainer, mainFragment).commit();
        }
    }
    public void switchToInventoryFragment(){
        fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.mainContainer);
        if (existingFragment == null){
            InventoryFragment inventoryFragment = InventoryFragment.newInstance("a","b");
            fragmentManager.beginTransaction().add(R.id.mainContainer, inventoryFragment).commit();
        }
        else {
            InventoryFragment inventoryFragment = InventoryFragment.newInstance("a","b");
            fragmentManager.beginTransaction().replace(R.id.mainContainer, inventoryFragment).commit();
        }
    }

    public Toolbar getToolbar(){
        return this.toolbar;
    }

    public void deleteUser(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("blow", "User deleted");
                    }
                });
    }

    public void signOut(final boolean signInAgain){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("blow", "signed out");
                        // delete from firestore db
                        DatabaseTransaction databaseTransaction = new DatabaseTransaction(MainActivity.this);
                        databaseTransaction.deleteUserFromDb();
                        //delete from firebase auth
                        deleteUser();
                        if (signInAgain){switchToMainFragment();}
                    }
                });

    }

    public void setUpNavDrawer(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
        nv = (NavigationView)findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Toast.makeText(getApplicationContext(),"works "+ String.valueOf(id) , Toast.LENGTH_SHORT).show();
                switch(id) {
                    case R.id.action_sign_out:
//                        deleteUser();
                        signOut(true);
                        Toast.makeText(MainActivity.this, "Signed out",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_inventory:
                        switchToInventoryFragment();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;

            }
        });
    }

    public void decideAndRemoveInventory(){
        // This removes "inventory" from nav drawer if the user is a consumer
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String mission = sharedPreferences.getString(getString(R.string.mission), null);
        Log.e("blow", "the mission stored is "+ mission);
        if (mission!=null && mission.equals("consumer") ){
            nv.getMenu().removeItem(R.id.action_inventory);
        }
    }


}
