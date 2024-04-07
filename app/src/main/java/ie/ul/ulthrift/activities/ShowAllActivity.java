package ie.ul.ulthrift.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.adaptors.ShowAllAdapter;
import ie.ul.ulthrift.models.ShowAllModel;

public class ShowAllActivity extends AppCompatActivity {

    // RecyclerView for displaying our items in a grid format
    RecyclerView recyclerView;
    // adapter used for ShowAllActivity
    ShowAllAdapter showAllAdapter;
    //List to hold the data got from firebase
    List<ShowAllModel> showAllModelList;
    //Needed so we can read data from the firebase firestore
    FirebaseFirestore firestore;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //good method for ui https://developer.android.com/develop/ui/views/layout/edge-to-edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_all);

        // Retrieve the type of items to display
        String type = getIntent().getStringExtra("type");

        // Initialise Firestore
        firestore = FirebaseFirestore.getInstance();
        //bind recycleviewer with our edge ui component
        recyclerView = findViewById(R.id.show_all_rec);
        //This is our  grid sort of layout setep so items displayed in a 1x2 row sort of thing
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        showAllModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this, showAllModelList);
        recyclerView.setAdapter(showAllAdapter);

        loadItemsOfType(type);
    }

    private void loadItemsOfType(String type) {
        // Used log for debugging as was astruggling to get type to load
        Log.d("ShowAllActivity", "Loading items of type: " + type);

        // make sure type is not null and not empty, meaning a specific category was requested to be shown
        if (type != null && !type.isEmpty()) {
            // Query Firestore for items of the specific type (whichever is queried)
            firestore.collection("ShowAll")
                    .whereEqualTo("type", type)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Create a new list to hold the filtered items (based of selection)
                            List<ShowAllModel> filteredList = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                // Convert each document to a ShowAllModel object
                                ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                                //Add them back to filterList
                                filteredList.add(showAllModel);
                            }
                            showAllAdapter.updateList(filteredList);
                            Log.d("ShowAllActivity", "Loaded " + filteredList.size() + " items of type: " + type);
                        } else {
                            Log.e("ShowAllActivity", "Error loading items", task.getException());
                        }
                    });
        } else {
            Log.d("ShowAllActivity", "Loading all items");

            // Same, but its just for our "showAll" to show al products
            firestore.collection("ShowAll")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<ShowAllModel> list = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                                list.add(showAllModel);
                            }
                            showAllAdapter.updateList(list);
                            Log.d("ShowAllActivity", "Loaded " + list.size() + " items in total");
                        } else {
                            Log.e("ShowAllActivity", "Error loading items", task.getException());
                        }
                    });
        }
    }
}