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

    // Method called when  delete button for a specific user listing is clicked
    @Override
    public void onDeleteClick(int position) {
        // Get selected model
        ShowAllModel selectedModel = myListings.get(position);
        // Get the document ID of the listing from the ShowAll collection
        String showAllDocId = selectedModel.getDocumentId();
        /*
        Get the document ID of the corresponding listing from the NewProducts collection
        As they are in seperate collections ids are different so now we post the ShowAll doc id
        In as a field(showAllDocId) of NewProducts collection so we can identify it and then get the right
        product to delete from NewProducts
        */
        String newProductsDocId = selectedModel.getNewProductDocId();

        // Make sure no errors with the ShowAllDocId
        if (showAllDocId != null && !showAllDocId.trim().isEmpty()) {
            //call method to delete from ShowAll
            deleteFromFirestore("ShowAll", showAllDocId, position);

            //Same for the coresspondeing product in NewProducts
            if (newProductsDocId != null && !newProductsDocId.trim().isEmpty()) {
                //call our New Products check and delete
                checkAndDeleteFromNewProducts(newProductsDocId);
            }
        } else {
            //error displaying if was one
            Toast.makeText(this, "Error: Document ID is null or empty, cannot delete the document.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check and delete the corresponding listing from the NewProducts collection
    private void checkAndDeleteFromNewProducts(String showAllDocId ) {
        /*
        Query NewProducts collection to find documents
        where the showAllDocId field matches the provided value
        */
        db.collection("NewProducts")
                .whereEqualTo("showAllDocId", showAllDocId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    //If it matches (which it should unless been up for more than 2 days, call delete method
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String newProductsDocId = document.getId();
                        deleteFromNewProducts(newProductsDocId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MyListingsActivity", "Error finding document in NewProducts collection", e);
                });
    }

    private void deleteFromNewProducts(String newProductsDocId) {
        //deletes corresponding from new Products
        db.collection("NewProducts").document(newProductsDocId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("MyListingsActivity", "Item deleted from NewProducts collection with docId: " + newProductsDocId);
                })
                .addOnFailureListener(e -> {
                    Log.e("MyListingsActivity", "Failed to delete item from NewProducts collection", e);
                });
    }

    private void deleteFromFirestore(String collection, String docId, int position) {
        //delete from ShowAll which is called earlier in onDeleteClick
        db.collection(collection).document(docId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore Delete", "Document with ID " + docId + " deleted from " + collection);
                    if (position >= 0) { // Only remove from the list if a valid position is passed
                        myListings.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore Delete", "Error deleting document", e));
    }
}

