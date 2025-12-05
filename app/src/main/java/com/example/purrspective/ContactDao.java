package com.example.purrspective;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    void insert(Contact contact);

    @Query("SELECT * FROM contacts")
    List<Contact> getAllContacts();

    @Delete
    void delete(Contact contact);

    @Query("UPDATE contacts SET styleId = :styleId WHERE id = :contactId")
    void updateStyleForContact(int contactId, int styleId);

    @Query("SELECT * FROM contacts WHERE id = :contactId LIMIT 1")
    Contact getContactById(int contactId);

    @Update
    void update(Contact contact);
}
