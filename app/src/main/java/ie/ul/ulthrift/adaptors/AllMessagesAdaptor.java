package ie.ul.ulthrift.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.activities.MessageActivity;
import ie.ul.ulthrift.models.UserModel;
import ie.ul.ulthrift.utils.FirebaseUtil;
import ie.ul.ulthrift.models.MessageRoomModel;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// Adapter for displaying all message rooms in a RecyclerView
public class AllMessagesAdaptor extends FirestoreRecyclerAdapter<MessageRoomModel, AllMessagesAdaptor.MessageRoomModelViewHolder> {

    // Context variable to store the context of the activity or fragment
    Context context;

    // Constructor to initialize the adapter with FirestoreRecyclerOptions and context
    public AllMessagesAdaptor(@NonNull FirestoreRecyclerOptions<MessageRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    // Bind data to views in each RecyclerView item
    @Override
    protected void onBindViewHolder(@NonNull MessageRoomModelViewHolder holder, int position, @NonNull MessageRoomModel model) {
        // Retrieve other user's information from the chat room document in Firestore
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Determine if the last message was sent by the current user
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        // Convert the Firestore document snapshot to a UserModel object
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        // Set the other user's name in the username TextView
                        if (otherUserModel != null) {
                            holder.usernameText.setText(otherUserModel.getName());
                        } else {
                            String unknownUserName = "Unknown";
                            holder.usernameText.setText(unknownUserName);
                        }

                        if (lastMessageSentByMe)
                            // If lastMessageSentByMe is true, this sets the last message in the lastMessage TextView and displays like that
                            holder.lastMessageText.setText("Me : " + model.getLastMessage());
                        else
                            // If lastMessageSentByMe is false, this sets the last message in the lastMessage TextView and displays like that
                            holder.lastMessageText.setText(model.getLastMessage());
                        // Set the last message timestamp
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        // Set click listener to navigate to message activity when item is clicked and brings the other users UID
                        holder.itemView.setOnClickListener(v -> {
                            //navigate to message activity
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("otherUserId", otherUserModel.getUid());
                            context.startActivity(intent);
                        });

                    }
                });
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MessageRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view by inflating the message_chat_row layout
        View view = LayoutInflater.from(context).inflate(R.layout.message_chat_row, parent, false);
        // Return a new ViewHolder instance
        return new MessageRoomModelViewHolder(view);
    }

    // ViewHolder class to hold references to views in each RecyclerView item
    static class MessageRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;

        // Constructor to initialize the ViewHolder with the item view
        public MessageRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
        }
    }
}