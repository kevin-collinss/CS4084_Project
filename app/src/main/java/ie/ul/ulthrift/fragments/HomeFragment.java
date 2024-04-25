package ie.ul.ulthrift.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.activities.ShowAllActivity;
import ie.ul.ulthrift.adaptors.CategoryAdaptor;
import ie.ul.ulthrift.adaptors.NewProductsAdaptor;
import ie.ul.ulthrift.models.CategoryModel;
import ie.ul.ulthrift.models.NewProductsModel;

public class HomeFragment extends Fragment {

    TextView catShowAll,newProductShowAll;
    LinearLayout linearLayout;
    AlertDialog progressDialog;
    RecyclerView catRecyclerview, newProductsRecyclerView;

    //Category recyclerview
    CategoryAdaptor categoryAdaptor;
    List<CategoryModel> categoryModelList;

    //New Products Recyclerview
    NewProductsAdaptor newProductsAdaptor;
    List<NewProductsModel> newProductsModelList;

    //FireStore
    FirebaseFirestore db;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Loading...");
        progressDialog = builder.create();
        catRecyclerview = root.findViewById(R.id.rec_category);
        newProductsRecyclerView = root.findViewById(R.id.new_product_rec);

        catShowAll = root.findViewById(R.id.category_see_all);
        newProductShowAll = root.findViewById(R.id.newProducts_see_all);

        catShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                startActivity(intent);
            }
        });

        newProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                startActivity(intent);
            }
        });



        db = FirebaseFirestore.getInstance();

        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);


        //Image slider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.mens_clothing, "Mens Clothing", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.womens_clothing, "Womens Clothing", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.shoes, "Shoes", ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);

        progressDialog.setTitle("Welcome To UL Thrifts");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Category
        catRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        categoryModelList = new ArrayList<>();
        categoryAdaptor = new CategoryAdaptor(getContext(),categoryModelList);
        catRecyclerview.setAdapter(categoryAdaptor);


        db.collection("Category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            CategoryModel categoryModel = document.toObject(CategoryModel.class);
                            categoryModelList.add(categoryModel);
                            categoryAdaptor.notifyDataSetChanged();
                            Log.d("FireStore", document.getId() + " => " + document.getData());
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error getting documents.", e);
                    }
                });

        //New Products
        newProductsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        newProductsModelList = new ArrayList<>();
        newProductsAdaptor = new NewProductsAdaptor(getContext(),newProductsModelList);
        newProductsRecyclerView.setAdapter(newProductsAdaptor);

        db.collection("NewProducts")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            NewProductsModel newProductsModel = document.toObject(NewProductsModel.class);
                            newProductsModelList.add(newProductsModel);
                            newProductsAdaptor.notifyDataSetChanged();
                            Log.d("FireStore", document.getId() + " => " + document.getData());
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error getting documents.", e);
                    }
                });

        linearLayout.setVisibility(View.VISIBLE);
        progressDialog.dismiss();

        return root;

    }

    public void onResume() {
        super.onResume();
       /*
         Called when the fragment is visible to the user and is running.
         when its resumed it calls loadNewProducts method.
       */
        loadNewProducts();
    }

    private void loadNewProducts() {
        // This method retrieves the new products from the Firestore database.
        Log.d("HomeFragment", "loadNewProducts() called");
        //show laoding dialog to inform whos logged in data is being loaded
        showProgressDialog();

        // Query the NewProducts collection from Firestore and get all the products
        db.collection("NewProducts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("HomeFragment", "Successfully got new products list, size: " + queryDocumentSnapshots.size());
                    newProductsModelList.clear(); // Clear the list to avoid duplication

                    // Iterate through the fetched documents and convert each to a NewProductsModel object.
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        NewProductsModel newProductsModel = document.toObject(NewProductsModel.class);
                        Log.d("HomeFragment", "Adding product: " + newProductsModel.getName());
                        newProductsModelList.add(newProductsModel);
                    }
                    // Notify the RecyclerView adapter that the data for new products has changed.
                    newProductsAdaptor.notifyDataSetChanged();
                    dismissProgressDialog(); //Or else it continues saying Welcome to UlThrifts
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment", "Error loading new products", e);
                    dismissProgressDialog();
                });
    }

    private void showProgressDialog() {
        // Checks if the progress dialog exists and is not already showing.
        // If it's not showing, it will display the dialog.
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        //Checks if the progress dialog is currently displayed.
        // If it is, it will dismiss the dialog to remove it from the screen.
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}