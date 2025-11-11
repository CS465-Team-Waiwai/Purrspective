package com.example.purrspective;

public class Contact {
    private String name;
    private int imageResId;

    public Contact(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
