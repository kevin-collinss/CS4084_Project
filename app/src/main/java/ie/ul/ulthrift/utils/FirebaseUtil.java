package ie.ul.ulthrift.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// Utility class for Firebase-related operations
public class FirebaseUtil {

    // Returns the current user's ID
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Returns a reference to the document of a given chat room ID
    public static DocumentReference getMessageRoomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("ChatRooms").document(chatroomId);
    }

    // Returns a reference to the collection of messages within a specified chat room
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getMessageRoomReference(chatroomId).collection("chats");
    }

    // Returns a reference to the collection called ChatRooms
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("ChatRooms");
    }


    // Returns a reference to the user document in Firestore based on the given userId
    public static DocumentReference getUserReference(String userId) {
        // Reference to the "users" collection where user documents are stored
        CollectionReference usersCollectionRef = FirebaseFirestore.getInstance().collection("users");

        // Reference to the document corresponding to the given userId
        return usersCollectionRef.document(userId);
    }

    // Returns a reference to the other user's document within the chat room based on a list of user IDs that are in the room
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return getUserReference(userIds.get(1));
        } else {
            return getUserReference(userIds.get(0));
        }
    }

    // Generates a chat room ID based on the user IDs in that room
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Converts a Firestore Timestamp to a string representation of time in "HH:mm" format
    public static String timestampToString(Timestamp timestamp) {
        // Create a SimpleDateFormat instance with the desired time format pattern
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        // Convert the Timestamp to a Date object
        Date date = timestamp.toDate();
        // Format the Date object using the SimpleDateFormat
        return formatter.format(date);
    }

}
