package com.example.notetaker.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notetaker.models.Note;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface NoteDao {

    //using @Insert instructs Room to insert all parameters into the db in a single transaction
    @Insert
    Single<Long> insertNote(Note note) throws Exception;

    //the @Update annotation modifies the set of entities given as parameters in the db
    //it uses a query that matches against the primary key of each entry in the entity
    @Update
    Single<Integer> updateNote(Note note) throws Exception;

    //the @Delete annotation deletes the entities passed in as parameters in the db
    @Delete
    Single<Integer> deleteNote(Note note) throws Exception;

    //@Query allows read/write operations on the db. Each @Query method is verified at compile time
    //Room also verifies the return type if the fields in the returned object don't match the entity column names
    @Query("SELECT * FROM notes")
    LiveData<List<Note>> getNotes();
 }
