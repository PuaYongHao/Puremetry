package com.example.puremetry;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class InstructionsUI extends AppCompatActivity implements View.OnClickListener {

    private ViewPager instructionsViewPager;
    private LinearLayout indicatorLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] dots;
    private Button nextButton;
    private Button backButton;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        // Set OnClickListener for button
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        instructionsViewPager = findViewById(R.id.instructionsViewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        sliderAdapter = new SliderAdapter();

        instructionsViewPager.setAdapter(sliderAdapter);

        displayDotsIndicator(0);

        instructionsViewPager.addOnPageChangeListener(viewListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton:
                if (nextButton.getText().toString().equals("Next"))
                    instructionsViewPager.setCurrentItem(currentPage + 1);
                else
                    InstructionsController.loadNoiseDetect(this);
                break;
            case R.id.backButton:
                instructionsViewPager.setCurrentItem(currentPage - 1);
                break;
        }
    }

    public void displayDotsIndicator(int position) {
        dots = new TextView[sliderAdapter.getCount()];
        indicatorLayout.removeAllViews();

        for (int count = 0; count < dots.length; count++) {
            dots[count] = new TextView(this);
            dots[count].setText(Html.fromHtml("&#8226"));
            dots[count].setTextSize(35);
            dots[count].setTextColor(getResources().getColor(R.color.light_grayish_blue));
            indicatorLayout.addView(dots[count]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.clicked_light_grayish_blue));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            displayDotsIndicator(position);
            currentPage = position;

            if (currentPage == 0) {
                nextButton.setEnabled(true);
                backButton.setEnabled(false);
                backButton.setVisibility(View.INVISIBLE);
                nextButton.setText("Next");
                backButton.setText("");
            } else if (currentPage == dots.length - 1) {
                nextButton.setEnabled(true);
                backButton.setEnabled(true);
                backButton.setVisibility(View.VISIBLE);
                nextButton.setText("Finish");
                backButton.setText("Back");
            } else {
                nextButton.setEnabled(true);
                backButton.setEnabled(true);
                backButton.setVisibility(View.VISIBLE);
                nextButton.setText("Next");
                backButton.setText("Back");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public class SliderAdapter extends PagerAdapter {

        LayoutInflater layoutInflater;

        // Arrays
        public int[] slide_images = {
                R.drawable.silence,
                R.drawable.headphones
        };

        public String[] slide_description = {
                "Find a quiet area to complete the hearing test.",
                "Connect your headphones to your device."
        };

        @Override
        public int getCount() {
            return slide_description.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (ConstraintLayout) object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.instructions_slide, container, false);

            ImageView slideImageView = view.findViewById(R.id.slideImageView);
            TextView slideTextView = view.findViewById(R.id.slideTextView);

            slideImageView.setImageResource(slide_images[position]);
            slideTextView.setText(slide_description[position]);

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ConstraintLayout) object);
        }
    }
}