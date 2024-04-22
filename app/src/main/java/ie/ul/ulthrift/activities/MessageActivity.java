package ie.ul.ulthrift.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.adaptors.MessageAdaptor;
import ie.ul.ulthrift.models.MessageModel;
import ie.ul.ulthrift.models.MessageRoomModel;
import ie.ul.ulthrift.utils.FirebaseUtil;

public class MessageActivity extends AppCompatActivity {

    String otherUserId;
    String messageRoomId;
    MessageRoomModel messageRoomModel;
    MessageAdaptor messageAdaptor;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;

    RecyclerView recyclerView;

    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Get userId of the seller
        otherUserId = getIntent().getStringExtra("otherUserId");

        // get/create message room id
        messageRoomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUserId);

        // Initialize UI components
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        // Handle back button click
        backBtn.setOnClickListener((v) -> {
            finish();
        });

        // Handle send message button click
        sendMessageBtn.setOnClickListener((v -> {
            // Gets message input as string and trims any trailing whitespace
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        // Handle drop pin button click
        ImageButton dropPinButton = findViewById(R.id.dropPinButton);
        dropPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to start Google Maps activity
                Intent intent = new Intent(MessageActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        getOrCreateMessageModel();
        setupMessageView();

        // Find and initialize SupportMapFragment from the layout associated with MapsActivity
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    //Setting up RecyclerView to display messages
    private void setupMessageView() {
        // Query to retrieve messages for the current specific chat room, ordered by timestamp in descending order, meaning the latest messages will appear first
        Query query = FirebaseUtil.getChatroomMessageReference(messageRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Configure options for FirestoreRecyclerAdapter
        // The FirestoreRecyclerAdapter binds a Query to a RecyclerView. When documents are added, removed, or change these updates are automatically applied to your UI in real time.
        // MessageModel.class instructs the adaptor to convert each DocumentSnapshot to a MessageModel object
        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();

        // Initialises new messageAdaptor
        messageAdaptor = new MessageAdaptor(options, getApplicationContext());

        // Linear Layout that manages the arrangement of the items in the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //This reverses the layout manager so new messages added will appear at the bottom
        linearLayoutManager.setReverseLayout(true);

        //This sets the layout manager to the recyclerView. It determines how items in the recyclerview will be laid out
        recyclerView.setLayoutManager(linearLayoutManager);

        //sets the messageAdaptor to the view.
        recyclerView.setAdapter(messageAdaptor);

        // This line starts listening to changes in the Firestore database
        messageAdaptor.startListening();

        // Automatically scrolls to the latest message when a new message is added
        messageAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    // Send a message to the other user
    private void sendMessageToUser(String message) {

        // Update the message room model with the latest message details
        messageRoomModel.setLastMessageTimestamp(Timestamp.now());
        messageRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        messageRoomModel.setLastMessage(message);

        // Update the message room document in Firestore database
        FirebaseUtil.getMessageRoomReference(messageRoomId).set(messageRoomModel);

        // Create a new message model object using the message input, the senders userId and the timestamp
        MessageModel chatMessageModel = new MessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());

        // Add the new message to the chat room in Firestore.
        // When completed successfully, clear the message input field
        // If failed, it informs the user
        FirebaseUtil.getChatroomMessageReference(messageRoomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText("");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show());
    }

    // gets or creates a message model if it doesn't exist
    private void getOrCreateMessageModel() {
        // Retrieve the message room model from Firestore
        FirebaseUtil.getMessageRoomReference(messageRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // If successful, it converts the Firestore document to a MessageRoomModel object
                messageRoomModel = task.getResult().toObject(MessageRoomModel.class);

                // If messageRoomModel is null, it means it is a first time chat so it creates a new one
                if (messageRoomModel == null) {
                    messageRoomModel = new MessageRoomModel(
                            messageRoomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUserId),
                            Timestamp.now(),
                            ""
                    );

                    //Saves the new message room model to Firestore
                    FirebaseUtil.getMessageRoomReference(messageRoomId).set(messageRoomModel);
                }
            }
        });

        // Retrieve the user document corresponding to otherUserId from Firestore
        FirebaseUtil.getUserReference(otherUserId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Retrieve the other user's name from the document
                String otherUserName = documentSnapshot.getString("name");
                if (otherUserName != null) {
                    // Set the other user's name in the UI
                    otherUsername.setText(otherUserName);
                } else {
                    // If name is not found, set it to otherUserId
                    otherUsername.setText(otherUserId);
                }
            } else {
                // If the user document does not exist, set the UI to otherUserId
                otherUsername.setText(otherUserId);
            }
        }).addOnFailureListener(e -> {
            // If there's a failure retrieving the user document, set the UI to otherUserId
            otherUsername.setText(otherUserId); // Set to otherUserId in case of failure
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Call setupMessageView() again in onResume() to ensure it's initialized
        setupMessageView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening for updates when the activity is paused
        if (messageAdaptor != null) {
            messageAdaptor.stopListening();
        }
    }


}