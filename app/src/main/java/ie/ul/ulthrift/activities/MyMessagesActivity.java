package ie.ul.ulthrift.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.fragments.AllMessagesFragment;

public class MyMessagesActivity extends AppCompatActivity {

    //Declaring allMessages Fragment
    AllMessagesFragment allMessagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        // Initialise the allMessagesFragment fragment
        allMessagesFragment = new AllMessagesFragment();

        //Load the fragment into the activity
        loadFragment(allMessagesFragment);
    }

    private void loadFragment(AllMessagesFragment allMessagesFragment) {
        // Start the new fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace the existing fragment in the main_frame_layout with allMessagesFragment
        transaction.replace(R.id.main_frame_layout, allMessagesFragment);
        //Commit the transaction
        transaction.commit();
    }
}