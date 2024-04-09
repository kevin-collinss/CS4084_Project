package ie.ul.ulthrift.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment;

    //Used for signing out
    FirebaseAuth auth;

    //need for handling firestore reference to new collection so can run a cleanup
    FirebaseFirestore firestore;

    //Toolbar variable to show our navbar in nav_bar.xml (menu)
    Toolbar navBar;

    //these used for our sidebar
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;


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



        //Get fire store instance
        firestore = FirebaseFirestore.getInstance();

        //remove new products called
        removeNewProducts();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setting up the toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, navBar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view
            int id = item.getItemId();

            if (id == R.id.nav_list_item) {
                Intent intent = new Intent(MainActivity.this, ListItemActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_messages) {
                // TODO messages page
            } else if (id == R.id.nav_view_all_items) {
                Intent intent = new Intent(MainActivity.this, ShowAllActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_my_listings) {
                Intent intent = new Intent(MainActivity.this, MyListingsActivity.class);
                startActivity(intent);

            } else if (id == R.id.nav_my_favourites) {
                Intent intent = new Intent(MainActivity.this, MyFavouritesActivity.class);
                startActivity(intent);

            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


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

    /*
    method checks when the main activity is launched if there are products on the new collection
    are more than 2 days since they have been listed. If they are it removes them from collection

    */


    private void removeNewProducts() {
        Date twoDaysAgo = getTwoDaysAgo();

        firestore.collection("NewProducts")
                .whereLessThanOrEqualTo("addDate", new Timestamp(twoDaysAgo))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete() // Delete the document if it hits the date check
                                    .addOnSuccessListener(aVoid -> Log.d("Cleanup", "Document deleted successfully"))
                                    .addOnFailureListener(e -> Log.e("Cleanup", "Error deleting document", e)); //log messages for logcat
                        }
                    } else {
                        Log.e("Cleanup", "Error getting documents: ", task.getException());
                    }
                });
    }

    //Gets the current date of the user when they are logged in
    private Date getTwoDaysAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        return calendar.getTime();
    }
}