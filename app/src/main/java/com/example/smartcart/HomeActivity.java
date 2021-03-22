package com.example.smartcart;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.smartcart.ui.search.ItemSearchFragment;
import com.example.smartcart.ui.shopping.ShoppingFragment;
import com.example.smartcart.ui.shopping.ShoppingViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoppingViewModel viewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shopping, R.id.navigation_not_shopping, R.id.navigation_camera)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        System.out.println(f.toString());
        System.out.println(getSupportFragmentManager().getBackStackEntryCount());
        System.out.println(getSupportFragmentManager().getFragments());
        if (f instanceof ItemSearchFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, ShoppingFragment.class, null).addToBackStack("").commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchView = menu.findItem(R.id.action_search);
        searchView.setVisible(false);
        this.invalidateOptionsMenu();

        return true;
    }
}