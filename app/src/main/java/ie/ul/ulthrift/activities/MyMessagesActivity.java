package ie.ul.ulthrift.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.fragments.AllMessagesFragment;

public class MyMessagesActivity extends AppCompatActivity {

    AllMessagesFragment allMessagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        allMessagesFragment = new AllMessagesFragment();
        loadFragment(allMessagesFragment);
    }

    private void loadFragment(AllMessagesFragment allMessagesFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame_layout,allMessagesFragment);
        transaction.commit();
    }
}