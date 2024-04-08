package ie.ul.ulthrift.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import ie.ul.ulthrift.R;
import ie.ul.ulthrift.adaptors.MyListingsAdapter;
import ie.ul.ulthrift.models.ShowAllModel;

import java.util.ArrayList;
import java.util.List;

public class MyListingsActivity extends AppCompatActivity implements MyListingsAdapter.OnItemClickListener {

    // Recycleviewer ssetup for showing the user listing
    private RecyclerView listingsRecyclerView;
    private List<ShowAllModel> myListings = new ArrayList<>();
    private MyListingsAdapter adapter;

    //Firebase for getting database and user logged in
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set layout from the xml file
        setContentView(R.layout.activity_my_listings);

        // Firebase setup
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Use myListing xml page for recycle viewer
        listingsRecyclerView = findViewById(R.id.my_listings_recycler_view);
        listingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialise the adapter for RecyclerView with an empty list and set 'this' as the c OnClickEventListener as we implement it
        adapter = new MyListingsAdapter(this, myListings, this);
        listingsRecyclerView.setAdapter(adapter);

        //call Load listing method of user currently logged in
        loadMyListings();
    }

    private void loadMyListings() {
        //Breaks if user not logged in (shouldn't ever break anyways shouldn't be able to get to this point)
        if (auth.getCurrentUser() != null) {
            // Query Firestore for documents in ShowAll collection with the userId field equal to the current user's Id whos logged in
            db.collection("ShowAll")
                    .whereEqualTo("userId", auth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        /*
                        Clear the current listings and add all the fetched documents to the myListings list
                        Set the document ID for each fetched document so it can later be deleted properly from collection.
                        Notify the adapter that the data set has changed so it can update the RecyclerView.
                        */
                        if (!queryDocumentSnapshots.isEmpty()) {
                            myListings.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                ShowAllModel showAllModel = documentSnapshot.toObject(ShowAllModel.class);
                                if (showAllModel != null) {
                                    showAllModel.setDocumentId(documentSnapshot.getId());
                                    myListings.add(showAllModel);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MyListingsActivity.this, "Error loading listings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    @Override
    public void onDeleteClick(int position) {

        //Get the document ID from the item at the specified position
        String documentId = myListings.get(position).getDocumentId();

        if (documentId != null && !documentId.trim().isEmpty()) {
            db.collection("ShowAll").document(documentId).delete()
                    .addOnSuccessListener(aVoid -> {
                        //use the document ID to delete the document from Firestore
                        myListings.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(MyListingsActivity.this, "Listing deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MyListingsActivity.this, "Error deleting listing", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(MyListingsActivity.this, "Error: Document ID is null or empty, cannot delete the document.", Toast.LENGTH_SHORT).show();
        }
    }
}

