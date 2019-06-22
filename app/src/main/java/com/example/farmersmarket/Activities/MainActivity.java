package com.example.farmersmarket.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.farmersmarket.Fragments.MainFragment;
import com.example.farmersmarket.Fragments.MapsFragment;
import com.example.farmersmarket.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    Toolbar toolbar;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        dl = (DrawerLayout)findViewById(R.id.drawer_layout);
        t = new ActionBarDrawerToggle(this,dl, R.string.open, R.string.close);
        dl.addDrawerListener(t);
        t.syncState();
        nv = (NavigationView)findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.action_sign_out:
                        Toast.makeText(MainActivity.this, "My Account",Toast.LENGTH_SHORT).show();break;
                }


                return true;

            }
        });

        Fragment mainFragment = fragmentManager.findFragmentById(R.id.mainContainer);
        if(mainFragment == null){
            mainFragment = MainFragment.newInstance("a", "b"); // just random arguments
        }
        fragmentManager.beginTransaction().add(R.id.mainContainer, mainFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void switchToMapsFragment(){
        fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.mainContainer);
        if (existingFragment == null){
            existingFragment = MapsFragment.newInstance("a","b");
            fragmentManager.beginTransaction().add(R.id.mainContainer, existingFragment).commit();
        }
        else {
            existingFragment = MapsFragment.newInstance("a","b");
            fragmentManager.beginTransaction().replace(R.id.mainContainer, existingFragment).commit();
        }
    }
    public void switchToMainFragment(){
        fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.mainContainer);
        if (existingFragment == null){
            existingFragment = MainFragment.newInstance("a","b");
            fragmentManager.beginTransaction().add(R.id.mainContainer, existingFragment).commit();
        }
        else {
            existingFragment = MainFragment.newInstance("a","b");
            fragmentManager.beginTransaction().replace(R.id.mainContainer, existingFragment).commit();
        }
    }

    public Toolbar getToolbar(){
        return this.toolbar;
    }
}
