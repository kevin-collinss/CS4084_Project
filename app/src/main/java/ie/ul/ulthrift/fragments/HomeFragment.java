package ie.ul.ulthrift.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

import ie.ul.ulthrift.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Image slider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.shoes, "Discount on Shoes", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.mens_clothing, "Mens Clothing", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.womens_clothing, "Womens Clothing", ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);

        return root;
    }
}