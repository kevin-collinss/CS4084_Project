package ie.ul.ulthrift.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

        public static String currentUserId(){
            return FirebaseAuth.getInstance().getUid();
        }


        public static DocumentReference getMessageRoomReference(String chatroomId){
            return FirebaseFirestore.getInstance().collection("ChatRooms").document(chatroomId);
        }

    // Assume this method returns a reference to the user document in Firestore based on the provided userId
    public static DocumentReference getUserReference(String userId) {
        // Reference to the "users" collection where user documents are stored
        CollectionReference usersCollectionRef = FirebaseFirestore.getInstance().collection("users");

        // Reference to the document corresponding to the provided userId
        return usersCollectionRef.document(userId);
    }

        public static String getChatroomId(String userId1,String userId2){
            if(userId1.hashCode()<userId2.hashCode()){
                return userId1+"_"+userId2;
            }else{
                return userId2+"_"+userId1;
            }
        }

}
