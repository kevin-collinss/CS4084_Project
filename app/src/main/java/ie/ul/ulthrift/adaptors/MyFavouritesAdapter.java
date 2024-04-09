package ie.ul.ulthrift.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.models.ShowAllModel;

import java.util.List;

public class MyFavouritesAdapter extends RecyclerView.Adapter<MyFavouritesAdapter.ViewHolder> {

    //Initalise context and favourites list of Object ShowAllModel
    private Context context;
    private List<ShowAllModel> favouritesList;

    //Firebase
    private FirebaseFirestore firestore;

    //Constructor initalising Context and favourites list
    public MyFavouritesAdapter(Context context, List<ShowAllModel> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the item layout and return a new ViewHolder.
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the current favourite item based on position
        ShowAllModel favouriteItem = favouritesList.get(position);
        // Bind the details of the favourite item to the ViewHolder.
        bindProductDetails(holder, favouriteItem);
    }

    private void bindProductDetails(ViewHolder holder, ShowAllModel favouriteItem) {
        // Extract the document id for the item from ShowAll collection, if its empty exit
        String documentId = favouriteItem.getNewProductDocId();
        if (documentId == null || documentId.trim().isEmpty()) {
            Log.e("Adapter", "Document ID is null or empty");
            return;
        }

        // Get the document from Firestore based on above and set the data on views if it exists.
        firestore.collection("ShowAll").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ShowAllModel showAllModel = documentSnapshot.toObject(ShowAllModel.class);
                        if (showAllModel != null) {
                            // Set the details for the view
                            holder.textViewProductName.setText(showAllModel.getName());
                            holder.textViewPrice.setText(String.format("Price: €%s", showAllModel.getPrice()));
                            Glide.with(context).load(showAllModel.getImg_url()).into(holder.imageViewProduct);
                        }
                    } else {
                        Log.e("Adapter", "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.e("Adapter", "Error getting document details", e));
    }

    //gets amount of items in the list
    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    // This provides a reference to the views for each product
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewPrice;

        // Constructor binds the views from the itemView.
        public ViewHolder(View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.favourite_img);
            textViewProductName = itemView.findViewById(R.id.favourite_product_name);
            textViewPrice = itemView.findViewById(R.id.favourite_price);
        }
    }
}

