package ie.ul.ulthrift.adaptors;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.models.MessageModel;
import ie.ul.ulthrift.utils.FirebaseUtil;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// Adapter for displaying chat messages in a RecyclerView
public class MessageAdaptor extends FirestoreRecyclerAdapter<MessageModel, MessageAdaptor.MessageModelViewHolder> {

    // Context variable to store the context of the activity or fragment
    Context context;

    // Constructor to initialize the adapter with FirestoreRecyclerOptions and context
    public MessageAdaptor(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    // Bind data to views in each RecyclerView item
    @Override
    protected void onBindViewHolder(@NonNull MessageModelViewHolder holder, int position, @NonNull MessageModel model) {
        // Check if the message sender is the current user
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            // If sender is current user, hide leftChatLayout and show rightChatLayout
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage()); // Set message in rightChatTextview
        } else {
            // If sender is not current user, hide rightChatLayout and show leftChatLayout
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage()); // Set message in leftChatTextview
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MessageModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the chat_message layout to create a new view
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message, parent, false);
        // Return a new ViewHolder instance
        return new MessageModelViewHolder(view);
    }

    static class MessageModelViewHolder extends RecyclerView.ViewHolder {

        // Layouts for sender and receiver messages
        LinearLayout leftChatLayout, rightChatLayout;
        // TextViews for sender and receiver messages
        TextView leftChatTextview, rightChatTextview;

        // Constructor to initialize the ViewHolder with the item view
        public MessageModelViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize Layouts and TextViews by finding them in the item view
            leftChatLayout = itemView.findViewById(R.id.sender_message_layout);
            rightChatLayout = itemView.findViewById(R.id.receiver_message_layout);
            leftChatTextview = itemView.findViewById(R.id.sender_message);
            rightChatTextview = itemView.findViewById(R.id.receiver_message);
        }
    }
}
