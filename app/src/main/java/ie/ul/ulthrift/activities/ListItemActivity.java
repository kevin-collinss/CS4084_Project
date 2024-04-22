package ie.ul.ulthrift.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ie.ul.ulthrift.R;

public class ListItemActivity extends AppCompatActivity {

    // UI stuff
    private EditText editItemName, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private ImageView imageView;

    // Data needed for listing an item
    private String itemName, itemDescription, itemImageUrl, itemCategory;
    private double itemPrice;

    // Firebase database stuff
    private FirebaseFirestore db;
    private StorageReference storageRef;

    // Activty launcher for selecting the image
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_activity);

        // Initialise Firebase Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Initialise UI Components
        editItemName = findViewById(R.id.editItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageView = findViewById(R.id.imageView);

        // Set up the spinner with the categories (men, women)
        setupSpinner();

        // Initialise image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                        uploadImageToFirebase(imageUri);
                    }
                }
        );
        // Set  listener for the upload image button
        findViewById(R.id.buttonUploadImage).setOnClickListener(view -> pickImage());

        // Set  listener for the submit button
        findViewById(R.id.buttonSubmit).setOnClickListener(view -> submitItem());

        // Initially disable the submit button so a user cannot submit an item without filling in all the fields
        findViewById(R.id.buttonSubmit).setEnabled(false);
    }

    private void pickImage() {
        //Sets the intent to open the image gallery and launch it
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //makes sure item name is entered before trying to upload to db
        itemName = editItemName.getText().toString().trim();
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please enter the item name first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct a unique file name to avoid name conflicts for images in db
        String imageName = "images/" + itemName + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(imageName);

        //start upload to firebase storage
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException(); //throws exception if one to throw
            }
            return imageRef.getDownloadUrl(); //downlaods image url
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemImageUrl = task.getResult().toString();
                findViewById(R.id.buttonSubmit).setEnabled(true); // Enable the submit button
            } else {
                Toast.makeText(this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); //toast error
                //message if it fails
            }
        });
    }


    private void setupSpinner() {
        // Creates and ArrayAdapter with the categories we defined (men, women)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_display_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);
    }



    private void submitItem() {
        // Get the values from the input fields
        itemName = editItemName.getText().toString().trim();
        itemDescription = editTextDescription.getText().toString().trim();
        String priceString = editTextPrice.getText().toString().trim();

        //new way of getting item category
        // Get the display name from the spinner
        String categoryDisplayArray = spinnerCategory.getSelectedItem().toString();

        /*
        values already setup for showing Mens clothing as mensclothing, Womens clothing womenscothing etc
        so was easier to setup a Display Names and acto values of them as String Arrays and loop through them
        */
        String[] displayNames = getResources().getStringArray(R.array.categories_display_array);
        String[] databaseValues = getResources().getStringArray(R.array.categories_values_array);
        for (int i = 0; i < displayNames.length; i++) {
            if (categoryDisplayArray.equals(displayNames[i])) {
                itemCategory = databaseValues[i];
                break;
            }
        }

        // Get the current user id of the user logged in from the Firebase Authentication
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //validates input before firestore db update is done
        if (validateInputs(itemName, itemDescription, priceString, itemCategory)) {
            // Parse the price input
            itemPrice = Double.parseDouble(priceString);

            //Get date of upload of item
            Timestamp addDate = new Timestamp(new Date());


            // Create a Map to store the values of the items
            Map<String, Object> item = new HashMap<>();
            item.put("name", itemName);
            item.put("description", itemDescription);
            item.put("price", itemPrice);
            item.put("type", itemCategory);
            item.put("img_url", itemImageUrl);
            item.put("addDate", addDate);
            //New to add the userid of the user who posted the item
            item.put("userId", currentUserId);

            // Add a new document with a generated ID to the "ShowAll" collection
            db.collection("ShowAll")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        // Retrieve the document ID of the newly added item
                        String showAllDocId = documentReference.getId();
                        db.collection("ShowAll").document(showAllDocId)
                                .update("newProductDocId", documentReference.getId())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "NewProductDocId updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to update NewProductDocId: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                        addToNewProducts(item, showAllDocId);
                        // Navigate back to the homepage
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle input validation failure
            Toast.makeText(this, "Please fill in all the fields correctly.", Toast.LENGTH_SHORT).show();
        }


    }

    private void addToNewProducts(Map<String, Object> item, String showAllDocId) {
        // Add the same item to 'NewProducts' collection
        item.put("showAllDocId", showAllDocId); // Add the reference to the 'ShowAll' document
        db.collection("NewProducts")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Item added to new products successfully", Toast.LENGTH_SHORT).show();
                    // Redirect or close activity as required
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add item to new products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private boolean validateInputs(String name, String description, String price, String category) {
        // Basic checks to make sure each input contains something
        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || category.isEmpty()) {
            return false;
        }

        try {
            // Check if price is a valid double
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return false;
        }

        return true; // all inputs are valid if gets to here
    }
}

