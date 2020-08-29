package com.example.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.entities.Note;

import java.util.List;


//second step
@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC ")
    List<Note> getAllNotes();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Delete
    void drleteNote(Note note);


}
