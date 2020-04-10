package com.example.notetaker;

import android.database.sqlite.SQLiteConstraintException;
import android.text.TextUtils;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.notetaker.models.Note;
import com.example.notetaker.util.LiveDataTestUtil;
import com.example.notetaker.util.TestUtil;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NoteDaoTest extends NoteDatabaseTest {

    public static final String TEST_TITLE = "This is a test title";
    public static final String TEST_CONTENT = "This is some test content";
    public static final String TEST_TIMESTAMP = "08-2018";

    //InstantTaskExecutorRule is used to swap background threads used by Architecture Components to a foreground thread
    //used in JUnit4 only, not Junit5
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void insertReadDelete() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);

        //insert
        getNoteDao().insertNote(note).blockingGet(); //blockingGet() waits for a response from a single, in a blocking fashion

        //read
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        List<Note> insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes());

        assertNotNull(insertedNotes);

        assertEquals(note.getContent(), insertedNotes.get(0).getContent());
        assertEquals(note.getTimestamp(), insertedNotes.get(0).getTimestamp());
        assertEquals(note.getTitle(), insertedNotes.get(0).getTitle());

        note.setId(insertedNotes.get(0).getId());
        assertEquals(note, insertedNotes.get(0));

        //delete
        getNoteDao().deleteNote(note).blockingGet();

        // confirm the database is empty
        insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes());
        assertEquals(0, insertedNotes.size());

    }

    @Test
    public void insertReadUpdateDelete() throws Exception{
        Note note = new Note(TestUtil.TEST_NOTE_1);
        //insert
        getNoteDao().insertNote(note).blockingGet();
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        List<Note> insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes());

        //read
        assertNotNull(insertedNotes);
        assertEquals(note.getContent(), insertedNotes.get(0).getContent());
        assertEquals(note.getTimestamp(), insertedNotes.get(0).getTimestamp());
        assertEquals(note.getTitle(), insertedNotes.get(0).getTitle());

        note.setId(insertedNotes.get(0).getId());
        assertEquals(note, insertedNotes.get(0));

        //update
        note.setContent(TEST_CONTENT);
        note.setTimestamp(TEST_TIMESTAMP);
        note.setTitle(TEST_TITLE);
        getNoteDao().updateNote(note).blockingGet();

        List<Note> updatedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes());

        assertNotNull(updatedNotes);
        assertEquals(TEST_CONTENT, updatedNotes.get(0).getContent());
        assertEquals(TEST_TIMESTAMP, updatedNotes.get(0).getTimestamp());
        assertEquals(TEST_TITLE, updatedNotes.get(0).getTitle());
        assertEquals(note, updatedNotes.get(0));

        //delete
        getNoteDao().deleteNote(note).blockingGet();

        updatedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes());
        assertEquals(0, updatedNotes.size());
    }


    @Test(expected = SQLiteConstraintException.class) //if the expected exception is thrown, test will pass
    public void insertNoteWithNullTitleThrowException() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);
        note.setTitle(null);
        getNoteDao().insertNote(note).blockingGet();
    }


    //Insert, update with null title, throw exception

    @Test(expected = SQLiteConstraintException.class)
    public void insertUpdateNoteWithNullTitleThrowException()  throws Exception{
        Note note = new Note(TestUtil.TEST_NOTE_1);
        //insert
        getNoteDao().insertNote(note).blockingGet();
        //read
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        List<Note> insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes());
        assertNotNull(insertedNotes);

        Note insertedNote = insertedNotes.get(0);
        insertedNote.setTitle(null);
        //update
        getNoteDao().updateNote(insertedNote).blockingGet();
    }
}
