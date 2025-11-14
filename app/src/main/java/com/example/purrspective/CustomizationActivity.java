package com.example.purrspective;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;

public class CustomizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customization);

        RecyclerView rv = findViewById(R.id.rvHorizontal);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        int[] images = {
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4 // add as many as you want
        };
        LinearLayout colorContainer = findViewById(R.id.colorContainer);

// 1. Array of colors you want
        int[] colors = {
                Color.parseColor("#FF6F61"),
                Color.parseColor("#6B5B95"),
                Color.parseColor("#88B04B"),
                Color.parseColor("#F7CAC9"),
                Color.parseColor("#92A8D1"),
                Color.parseColor("#034F84")
        };

// 2. Track the currently selected circle
        final View[] selectedView = { null };

        for (int c : colors) {
            // Inflate the circle layout
            View v = getLayoutInflater().inflate(R.layout.color_circle, colorContainer, false);

            // Apply the actual color
            v.getBackground().setTint(c);

            // Set padding so border is visible
            v.setPadding(6, 6, 6, 6);

            // Click listener for selection
            v.setOnClickListener(view -> {

                // Remove border from previously selected color
                if (selectedView[0] != null) {
                    selectedView[0].getBackground().setTintMode(android.graphics.PorterDuff.Mode.SRC_IN);
                    ((android.graphics.drawable.GradientDrawable) selectedView[0].getBackground())
                            .setStroke(4, Color.TRANSPARENT);
                }

                // Add green border to this circle
                ((android.graphics.drawable.GradientDrawable) v.getBackground())
                        .setStroke(4, Color.GREEN);

                // Store selected view
                selectedView[0] = v;
            });

            // Add circle to the layout
            colorContainer.addView(v);
        }

        rv.setAdapter(new ImageCarouselAdapter(images));
    }
}
