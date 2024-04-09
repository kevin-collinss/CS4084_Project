    package ie.ul.ulthrift.activities;

    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QueryDocumentSnapshot;

    import ie.ul.ulthrift.R;
    import ie.ul.ulthrift.adaptors.MyFavouritesAdapter;
    import ie.ul.ulthrift.models.ShowAllModel;

    import java.util.ArrayList;
    import java.util.List;

    public class MyFavouritesActivity extends AppCompatActivity {

        //Recycler viewer used for favourite items
        private RecyclerView recyclerView;
        //Adapter for recycleviewer
        private MyFavouritesAdapter adapter;
        //List to hold object of ShowALlModel that correspond with items favourited
        private List<ShowAllModel> favouritesList;
        //Firebase
        private FirebaseFirestore db;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //xml layout
            setContentView(R.layout.activity_favourites);

            //initalise recycleviewer
            recyclerView = findViewById(R.id.favourites_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            //initalise favourite list and adapter
            favouritesList = new ArrayList<>();
            adapter = new MyFavouritesAdapter(this, favouritesList);
            recyclerView.setAdapter(adapter);

            db = FirebaseFirestore.getInstance();
            //call loadFavourites to load user favourites
            loadFavourites();
        }

        private void loadFavourites() {
            //get current user and make sure not null
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                //store it as userId
                String userId = auth.getCurrentUser().getUid();
                //get that specific users favoruites
                db.collection("Favourites").whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            //Clear list first and then reget the list incase its been updated
                            favouritesList.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                DocumentReference showAllRef = document.getDocumentReference("showAllRef");
                                if (showAllRef != null) {
                                    fetchShowAllDetails(showAllRef.getPath());
                                } else {
                                    Toast.makeText(MyFavouritesActivity.this, "Error displaying favourites", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MyFavouritesActivity", "Error adding to favourites", e);

                        });
            }
        }

        private void fetchShowAllDetails(String refPath) {
            // Use document path to get the document snapshot from Firestore
            FirebaseFirestore.getInstance().document(refPath).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        //check if the document is found
                        if (documentSnapshot.exists()) {
                            //convert the snapshot to ShowAllModel object
                            ShowAllModel showAllModel = documentSnapshot.toObject(ShowAllModel.class);
                            if (showAllModel != null) {
                                // If  successful, add the ShowAllModel object to the favourites list i.e get the products
                                favouritesList.add(showAllModel);
                                //update adapter about changes
                                adapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MyFavouritesActivity", "Error fetching details of products" );
                    });
        }
    }