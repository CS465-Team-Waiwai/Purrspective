package com.example.purrspective;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
ImageView image1, image2;
Drawable greenOutline;
public class CustomizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customization);
        image1 = findViewById(R.id.image_style1);
        image2 = findViewById(R.id.image_style2);

        greenOutline = getResources().getDrawable(R.drawable.green_outline);

        image1.setOnClickListener(v -> {
            image1.setBackground(greenOutline);     // highlight
            image2.setBackground(null);             // remove highlight
        });

        image2.setOnClickListener(v -> {
            image2.setBackground(greenOutline);
            image1.setBackground(null);
        });


        /* TODO: Complete the functionality */
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // doesn't do anything yet, it just quits this activity
                finish();
            }
        });
    }
}
