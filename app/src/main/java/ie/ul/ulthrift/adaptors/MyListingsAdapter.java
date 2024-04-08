package ie.ul.ulthrift.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ie.ul.ulthrift.R;
import ie.ul.ulthrift.models.ShowAllModel;

import java.util.List;

public class MyListingsAdapter extends RecyclerView.Adapter<MyListingsAdapter.ViewHolder> {

    private Context context;
    //data adapter will use to create the ViewHolder items
    private List<ShowAllModel> listings;

    // Listener for click events on the delete button within each RecyclerView item
    private OnItemClickListener listener;

    // Interface that is implemented by MyListingActivity that creates an instance of MyListingAdapter.
    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    // Constructor of the adapter
    public MyListingsAdapter(Context context, List<ShowAllModel> listings, OnItemClickListener listener) {
        this.context = context;
        this.listings = listings;
        this.listener = listener;
    }

    // This method is called when the RecyclerView needs a new ViewHolder. This holds view for RecycleViewer Items
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        return new ViewHolder(view, listener);
    }

    // Binding the data from the listing to the actual view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShowAllModel item = listings.get(position);
        Glide.with(context).load(item.getImg_url()).into(holder.imageView);
        holder.textViewName.setText(item.getName());
        // You can set other details here, as needed.
    }

    // Getting the size of the listings list
    @Override
    public int getItemCount() {
        return listings.size();
    }

    // ViewHolder class describes an item view and metadata about its place within the RecyclerView. Uses
    // the xml file design buttons and text
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewName;
        public Button deleteButton;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            textViewName = itemView.findViewById(R.id.item_name);
            deleteButton = itemView.findViewById(R.id.delete_button);

            // Handling the delete button click event
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position); // position is passed here
                    }
                }
            });
        }
    }

}