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
                            linearLayout.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
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


        return root;
    }
}