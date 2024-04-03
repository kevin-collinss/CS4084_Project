package ie.ul.ulthrift.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment;

    //Used for signing out
    FirebaseAuth auth;

    //Toolbar variable to show our navabr in nav_bar.xml (menu)
    Toolbar navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialising our firebase auth to get their login state (should be logged in at this point)
        auth = FirebaseAuth.getInstance();

        //Initialising nav bar
        navBar = findViewById(R.id.home_nav_bar);
        setSupportActionBar(navBar);
        //When making a menu u get this horrible black text of the title of app so just
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Enabling our more options icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Calling this up indicator as our menu icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon);

        //Initialising HomeFragment to be used for display in the main content area.
        homeFragment = new HomeFragment();
        //Loading the HomeFragment
        loadFragment(homeFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating nav_bar.xml into the menu passed as a parameter to create the options menu.
        getMenuInflater().inflate(R.menu.nav_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handlingselection in the options menu by getting the item id.
        int id = item.getItemId();

        if (id == R.id.nav_bar_logout) {
            //If id is logout, signout and go back to registration
            auth.signOut();
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            finish();
            //TODO - deal with options bar and getting nice display for it and what other pages to include
        }
        return true;
    }

    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container,homeFragment);
        transaction.commit();
    }
}