package ie.ul.ulthrift.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {


        public static String currentUserId(){
            return FirebaseAuth.getInstance().getUid();
        }

        public static DocumentReference getMessageRoomReference(String chatroomId){
            return FirebaseFirestore.getInstance().collection("ChatRooms").document(chatroomId);
        }

        public static String getChatroomId(String userId1,String userId2){
            if(userId1.hashCode()<userId2.hashCode()){
                return userId1+"_"+userId2;
            }else{
                return userId2+"_"+userId1;
            }
        }

}
