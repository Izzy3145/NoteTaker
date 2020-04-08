package com.example.notetaker.di;

import android.app.Application;

import androidx.room.Room;

import com.example.notetaker.db.NoteDao;
import com.example.notetaker.db.NoteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.example.notetaker.db.NoteDatabase.DATABASE_NAME;

@Module
public class AppModule {

    @Singleton
    @Provides
    static NoteDatabase provideNoteDatabase(Application application){
        return Room.databaseBuilder(
                application,
                NoteDatabase.class,
                DATABASE_NAME
        ).build();
    }

    @Singleton
    @Provides
    static NoteDao provideNoteDao(NoteDatabase noteDatabase){
        return noteDatabase.getNoteDao();
    }

}