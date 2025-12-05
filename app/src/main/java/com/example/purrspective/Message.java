package com.example.purrspective;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int contactId;
    private String text;
    private int stickerResId;
    private long timestamp;

    public Message(int contactId, String text, int stickerResId, long timestamp) {
        this.contactId = contactId;
        this.text = text;
        this.stickerResId = stickerResId;
        this.timestamp = timestamp;
    }

    // --- getters & setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStickerResId() {
        return stickerResId;
    }

    public void setStickerResId(int stickerResId) {
        this.stickerResId = stickerResId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
