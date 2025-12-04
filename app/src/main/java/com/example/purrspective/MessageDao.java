package com.example.purrspective;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Query("SELECT * FROM messages WHERE contact_id = :contactId ORDER BY timestamp ASC")
    List<Message> getMessagesForContact(int contactId);
}
