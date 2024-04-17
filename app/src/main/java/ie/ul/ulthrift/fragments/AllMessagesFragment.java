package ie.ul.ulthrift.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.adaptors.AllMessagesAdaptor;
import ie.ul.ulthrift.models.MessageRoomModel;
import ie.ul.ulthrift.utils.FirebaseUtil;

// Fragment for displaying all messages in a RecyclerView
public class AllMessagesFragment extends Fragment {

    // RecyclerView to display all messages
    RecyclerView recyclerView;
    // Adapter for populating the RecyclerView
    AllMessagesAdaptor adapter;

    // Default constructor
    public AllMessagesFragment() {
    }

    // Called to create and return the view hierarchy associated with the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_messages, container, false);
        // Find the RecyclerView in the layout
        recyclerView = view.findViewById(R.id.messages_view_container);
        // Setup RecyclerView with data and adapter
        setupRecyclerView();
        // Return the inflated view
        return view;
    }

    // Setup RecyclerView with Firestore data and adapter
    private void setupRecyclerView() {

        // Construct the Firestore query to fetch all chat rooms where the current user is a member of and order them by timestamp in descending order
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        // Configure options for the FirestoreRecyclerAdapter
        FirestoreRecyclerOptions<MessageRoomModel> options = new FirestoreRecyclerOptions.Builder<MessageRoomModel>()
                .setQuery(query, MessageRoomModel.class).build();

        // Create a new adapter with the options and context
        adapter = new AllMessagesAdaptor(options, getContext());
        // Set layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Set adapter for RecyclerView
        recyclerView.setAdapter(adapter);
        // Start listening for Firestore data changes
        adapter.startListening();

    }

    // Called when the fragment is visible to the user
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    // Called when the fragment is no longer started
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    // Called when the fragment is visible to the user and actively running
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            // Notify the adapter that the data set has changed
            adapter.notifyDataSetChanged();
    }
}