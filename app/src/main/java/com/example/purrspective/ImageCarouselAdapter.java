package com.example.purrspective;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for a horizontal image carousel.
 * Pass an array of drawable resource IDs to display the images.
 */
public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ViewHolder> {

    private int[] images; // drawable resource IDs

    // Constructor
    public ImageCarouselAdapter(int[] images) {
        this.images = images;
    }

    // Inflate item layout and return ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_carousel, parent, false);
        return new ViewHolder(view);
    }

    // Bind image resource to ImageView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }

    // Return number of items
    @Override
    public int getItemCount() {
        return images.length;
    }

    // ViewHolder inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItem);
        }
    }
}
