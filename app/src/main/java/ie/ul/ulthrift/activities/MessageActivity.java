package ie.ul.ulthrift.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);



        //Get other userId
        otherUserId = getIntent().getStringExtra("otherUserId");
        messageRoomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUserId);

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        //back btn
        backBtn.setOnClickListener((v)->{
            finish();
        });

        otherUsername.setText(otherUserId);

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));


        getOrCreateMessageModel();
        setupMessageView();

    }

    private void setupMessageView() {
        Query query = FirebaseUtil.getChatroomMessageReference(messageRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query,MessageModel.class).build();

        messageAdaptor = new MessageAdaptor(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(messageAdaptor);
        messageAdaptor.startListening();
        messageAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message) {
        messageRoomModel.setLastMessageTimestamp(Timestamp.now());
        messageRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        messageRoomModel.setLastMessage(message);
        FirebaseUtil.getMessageRoomReference(messageRoomId).set(messageRoomModel);

        MessageModel chatMessageModel = new MessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(messageRoomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }

                });
    }

    private void getOrCreateMessageModel(){
        FirebaseUtil.getMessageRoomReference(messageRoomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                messageRoomModel = task.getResult().toObject(MessageRoomModel.class);
                if(messageRoomModel==null){
                    //first time chat
                    messageRoomModel = new MessageRoomModel(
                            messageRoomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUserId),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getMessageRoomReference(messageRoomId).set(messageRoomModel);
                }
            }
        });

        FirebaseUtil.getUserReference(otherUserId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String otherUserName = documentSnapshot.getString("name");
                if (otherUserName != null) {
                    otherUsername.setText(otherUserName);
                } else {
                    otherUsername.setText(otherUserId); // Set to otherUserId if name is not found
                }
            } else {
                otherUsername.setText(otherUserId); // Set to otherUserId if user document does not exist
            }
        }).addOnFailureListener(e -> {
            otherUsername.setText(otherUserId); // Set to otherUserId in case of failure
        });

    }


}