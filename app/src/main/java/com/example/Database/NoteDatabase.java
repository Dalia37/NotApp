package com.example.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.Dao.NoteDao;
import com.example.entities.Note;

//third_step
@Database(entities = Note.class, version = 1 , exportSchema = false)
public abstract class  NoteDatabase extends RoomDatabase {

    private static  NoteDatabase noteDatabase ;

    public static synchronized NoteDatabase getDataBase(Context context){
        if (noteDatabase ==null){
            noteDatabase = Room.databaseBuilder(context,NoteDatabase.class,"notes_db").build();
        }
        return noteDatabase ;

    }
    public abstract NoteDao noteDao();
}
