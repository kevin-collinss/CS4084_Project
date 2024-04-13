package ie.ul.ulthrift.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Arrays;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.models.MessageRoomModel;
import ie.ul.ulthrift.utils.FirebaseUtil;

public class MessageActivity extends AppCompatActivity {

    String otherUserId;
    String messageRoomId;
    MessageRoomModel messageRoomModel;
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

        getOrCreateMessageModel();

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
    }


}