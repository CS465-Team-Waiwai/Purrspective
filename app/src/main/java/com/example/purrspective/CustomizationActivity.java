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

public class CustomizationActivity extends AppCompatActivity {

    private int selectedStyle=-1;
    private int ContactID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customization);
        ContactID = getIntent().getIntExtra("CONTACT_ID", -1);
        ImageView image1 = findViewById(R.id.image_style1);
        ImageView image2 = findViewById(R.id.image_style2);

        Drawable greenOutline = getResources().getDrawable(R.drawable.green_outline);

        image1.setOnClickListener(v -> {
            selectedStyle=1;
            image1.setBackground(greenOutline);     // highlight
            image2.setBackground(null);
            SaveStyleToDatabase(1);// remove highlight
        });

        image2.setOnClickListener(v -> {
            selectedStyle=2;
            image2.setBackground(greenOutline);
            image1.setBackground(null);
            SaveStyleToDatabase(2);
        });


        /* TODO: Complete the functionality */
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // doesn't do anything yet, it just quits this activity
                Intent result = new Intent();
                result.putExtra("SELECTED_STYLE", selectedStyle);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    private void SaveStyleToDatabase(int styleValue) {
        AppDatabase db = AppDatabase.getInstance(this);
        db.contactDao().updateStyleForContact(ContactID, styleValue);
        Toast.makeText(this, "style updated!", Toast.LENGTH_SHORT).show();
    }

    public int getSelectedStyle() {
        return selectedStyle;
    }
}
