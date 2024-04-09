package ie.ul.ulthrift.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.models.NewProductsModel;
import ie.ul.ulthrift.models.ShowAllModel;

public class DetailedActivity extends AppCompatActivity {

    //log tag
    private static final String TAG = "DetailedActivity";

    ImageView detailedImg;
    TextView name, description, price;
    Button addToFavourites, messageSeller;

    // New Products
    NewProductsModel newProductsModel = null;

    // Show All
    ShowAllModel showAllModel = null;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        firestore = FirebaseFirestore.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");

        // Logging
        Log.d(TAG, "Received object type: " + obj.getClass().getSimpleName());

        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        } else if (obj instanceof ShowAllModel) {
            showAllModel = (ShowAllModel) obj;
        }

        detailedImg = findViewById(R.id.detailed_img);
        name = findViewById(R.id.detailed_name);
        description = findViewById(R.id.detailed_desc);
        price = findViewById(R.id.detailed_price);

        addToFavourites = findViewById(R.id.add_to_favourites);
        messageSeller = findViewById(R.id.message_seller);

        //New Products
        if (newProductsModel != null) {
            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            description.setText(newProductsModel.getDescription());
            price.setText(String.valueOf(newProductsModel.getPrice()));

            Log.d(TAG, "Displaying New Products Model");
        }

        //Show All Products
        if (showAllModel != null) {
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailedImg);
            name.setText(showAllModel.getName());
            description.setText(showAllModel.getDescription());
            price.setText(String.valueOf(showAllModel.getPrice()));

            Log.d(TAG, "Displaying Show All Model");
        }

        addToFavourites.setOnClickListener(v -> {
            // Assuming that newProductsModel has a reference to the ShowAll ID
            addFavouriteItem();
        });

        // Call get userId methods in NewProducts and ShowAll depending what view is called
        String productUserId = (newProductsModel != null) ? newProductsModel.getUserId() :
                (showAllModel != null) ? showAllModel.getUserId() : "";

        //get current userid
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if user logged in is actually viewing own product
        if(productUserId.equals(currentUserId)) {
            // Hide both buttons if the user is viewing own listing
            findViewById(R.id.add_to_favourites).setVisibility(View.GONE);
            findViewById(R.id.message_seller).setVisibility(View.GONE);
        } else {
            //Else call itemId method and see if already favoruited and go on from there
            String itemId = getItemId();
            checkFavouriteStatus(itemId);
            addToFavourites.setOnClickListener(v -> addFavouriteItem());
        }
    }


    private void addFavouriteItem() { //Dont actually need to pass objects
        //Gets userid of current user. Checks if its null too just incase somehow bypass
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null) {
            Log.e(TAG, "User ID is null. User might not be logged in.");
            return;
        }

        // Get the product model that is passed
        String referenceId = (showAllModel != null) ? showAllModel.getNewProductDocId() :
                (newProductsModel != null) ? newProductsModel.getShowAllDocId() : null;

        if (referenceId == null) {
            Log.e(TAG, "Reference ID is null. Cannot add to favourites.");
            return;
        }

        // Create a document reference to the "ShowAll" collection using the reference ID
        DocumentReference showAllRef = firestore.collection("ShowAll").document(referenceId);

        //Add these fo A favItem hashmap, main thing is the showAllRef document reference
        Map<String, Object> favItem = new HashMap<>();
        favItem.put("userId", userId);
        favItem.put("showAllRef", showAllRef);
        favItem.put("timestamp", FieldValue.serverTimestamp());
        //Add these to the favourites collection
        firestore.collection("Favourites")
                .add(favItem)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Successfully added to Favourites: " + documentReference.getId());
                    Toast.makeText(DetailedActivity.this, "Added to Favourites!", Toast.LENGTH_SHORT).show();
                    updateFavouriteButton(true); // change favourite button to say "favourited"
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding to favourites", e);
                    Toast.makeText(DetailedActivity.this, "Error adding to favourites", Toast.LENGTH_SHORT).show();
                });
    }

    //get the Id of the item whether viewed from ShowALl or NewProducts Colletion
    private String getItemId() {
        if (newProductsModel != null) {
            return newProductsModel.getShowAllDocId();
        } else if (showAllModel != null) {
            return showAllModel.getNewProductDocId();
        }
        return null;
    }

    private void checkFavouriteStatus(String itemId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // pass document reference path stored as a String
        String referencePath = "ShowAll/" + itemId;
        //A check if the user logged in has already favourited that item and update button if they have
        firestore.collection("Favourites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("showAllRef", firestore.document(referencePath))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Item is already favourited by this user
                        updateFavouriteButton(true);
                    } else {
                        // Item is not favourited
                        updateFavouriteButton(false);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking favourites status", e));
    }

    private void updateFavouriteButton(boolean isFavourited) {
        //Get the Add to Favourites button
        Button addToFavourites = findViewById(R.id.add_to_favourites);
        //If the product is set to favourited,  Set the text to Favourited
        if (isFavourited) {
            addToFavourites.setText("Favourited");
            addToFavourites.setEnabled(false);
        } else {
            //Display what the usual should be
            addToFavourites.setText("Add to Favourites");
            addToFavourites.setEnabled(true);
        }
    }
}