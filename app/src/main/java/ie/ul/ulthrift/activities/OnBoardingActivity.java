package ie.ul.ulthrift.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ie.ul.ulthrift.R;
import ie.ul.ulthrift.adaptors.SliderAdapter;

public class OnBoardingActivity extends AppCompatActivity {

    // Declare the ViewPager and LinearLayout for the dots indicators
    ViewPager viewPager;
    LinearLayout dotsLayout;

    // Declare the adapter for the ViewPager and the Button for user interactioh
    SliderAdapter sliderAdapter;
    Button btn;

    // TextView array to hold the dots
    TextView[] dots;

    // Animation for button transition
    Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        //Initialise the ViewPager and dots Layout from the xml
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        btn = findViewById(R.id.get_started_btn);

        addDots(0);

        //Set the ViewPager change listener
        viewPager.addOnPageChangeListener(changeListener);

        //Call Adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        // Set the OnClickListener for the button to navigate to the RegistrationActivity when the "onboarding is finished
        btn.setOnClickListener(v -> {
            startActivity(new Intent(OnBoardingActivity.this, RegistrationActivity.class));
            // Close the OnBoardingActivity once it's done
            finish();
        });
    }

    //Method to add dot indicators to the LinearLayout based on the current position
    private void addDots(int position){

        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for(int i = 0; i < dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.ulGreen));
        }
    }

    //Create an  implementation of OnPageChangeListener
    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        // This method invoked when the current page is scrolled
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        // Method is called when a new page becomes selected.
        @Override
        public void onPageSelected(int position) {
            addDots(position);
            if(position == 0){
                // Hide the button on the first and second page and show it on the last
                btn.setVisibility(View.INVISIBLE);
            }else if (position == 1){
                btn.setVisibility(View.INVISIBLE);
            }else{
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this,R.anim.slide_animation);
                btn.setAnimation(animation);
                btn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Called when the scroll state changes.
        }
    };
}