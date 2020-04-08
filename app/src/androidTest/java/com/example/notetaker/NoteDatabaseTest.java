package com.example.notetaker;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.notetaker.db.NoteDao;
import com.example.notetaker.db.NoteDatabase;

import org.junit.After;
import org.junit.Before;

public class NoteDatabaseTest {

    private NoteDatabase noteDatabase;

    public NoteDao getNoteDao(){
        return noteDatabase.getNoteDao();
    }

    @Before
    public void init(){
        //inMemoryDatabaseBuilder creates an in-memory database, who's data is wiped when it is closed
        noteDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                NoteDatabase.class).build();
    }

    @After
    public void finish(){
        noteDatabase.close();
    }
}
