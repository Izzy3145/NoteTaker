package com.example.notetaker.di;

import android.app.Application;

import androidx.room.Room;

import com.example.notetaker.db.NoteDao;
import com.example.notetaker.db.NoteDatabase;
import com.example.notetaker.repository.NoteRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.example.notetaker.db.NoteDatabase.DATABASE_NAME;

@Module
class AppModule {

    @Singleton
    @Provides
    static NoteDatabase provideNoteDatabase(Application application) {
        return Room.databaseBuilder(
                application,
                NoteDatabase.class,
                DATABASE_NAME
        ).build();
    }

    @Singleton
    @Provides
    static NoteDao provideNoteDao(NoteDatabase noteDatabase) {
        return noteDatabase.getNoteDao();
    }

    @Singleton
    @Provides
    static NoteRepository provideNoteRepository(NoteDao noteDao){
        return new NoteRepository(noteDao);
    }
}
