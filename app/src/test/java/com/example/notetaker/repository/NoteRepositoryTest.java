package com.example.notetaker.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.notetaker.db.NoteDao;
import com.example.notetaker.models.Note;
import com.example.notetaker.ui.Resource;
import com.example.notetaker.util.InstantExecutorExtension;
import com.example.notetaker.util.LiveDataTestUtil;
import com.example.notetaker.util.TestUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static com.example.notetaker.repository.NoteRepository.DELETE_FAILURE;
import static com.example.notetaker.repository.NoteRepository.DELETE_SUCCESS;
import static com.example.notetaker.repository.NoteRepository.INSERT_FAILURE;
import static com.example.notetaker.repository.NoteRepository.INSERT_SUCCESS;
import static com.example.notetaker.repository.NoteRepository.INVALID_NOTE_ID;
import static com.example.notetaker.repository.NoteRepository.NOTE_TITLE_NULL;
import static com.example.notetaker.repository.NoteRepository.UPDATE_FAILURE;
import static com.example.notetaker.repository.NoteRepository.UPDATE_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.reactivex.Single;

import static com.example.notetaker.util.TestUtil.TEST_NOTE_1;
import static org.mockito.Mockito.when;

@ExtendWith(InstantExecutorExtension.class)
public class NoteRepositoryTest {

    private static final Note NOTE1 = new Note(TestUtil.TEST_NOTE_1);

    //system under test
    private NoteRepository noteRepository;

    //this is a unit test, so unlike instrumentation tests, it cannot depend on the Android Framework
    //therefore, we use Mockito to mock components that utilise components of the Android Framework
    private NoteDao noteDao;

    //this method will be called before each of the @Tests
    //we want a fresh DAO and repository before each test, therefore use this   
    @BeforeEach
    public void init(){
        noteDao = mock(NoteDao.class);
        noteRepository = new NoteRepository(noteDao);
    }

    @Test
    void insertNote_returnRow() throws Exception{
        //arrange
        final Long insertedRow = 1L;
        final Single<Long> returnedData = Single.just(insertedRow);
        when(noteDao.insertNote(any(Note.class))).thenReturn(returnedData);

        //act
        final Resource<Integer> returnedValue = noteRepository.insertNote(NOTE1).blockingFirst(); //use blockingFirst when returning a Flowable

        verify(noteDao).insertNote(any(Note.class));
        verifyNoMoreInteractions(noteDao);

        //assert
        assertEquals(Resource.success(1, INSERT_SUCCESS), returnedValue);
    }

    @Test
    void insertNote_returnFail() throws Exception {
        final Long insertedRow = -1L;
        final Single<Long> returnedData = Single.just(insertedRow);
        when(noteDao.insertNote(any(Note.class))).thenReturn(returnedData);

        final Resource<Integer> returnedValue = noteRepository.insertNote(NOTE1).blockingFirst(); //use blockingFirst when returning a Flowable

        verify(noteDao).insertNote(any(Note.class));
        verifyNoMoreInteractions(noteDao);

        //assert
        assertEquals(Resource.error(null, INSERT_FAILURE), returnedValue);
    }

    @Test
    void insertNoteNullTitle_throwsException() throws Exception{
        Exception exception = assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Note note = new Note(TEST_NOTE_1);
                note.setTitle(null);

                noteRepository.insertNote(note);
            }
        });

        assertEquals(NOTE_TITLE_NULL, exception.getMessage());
    }

    @Test
    void updateRow_returnRow() throws Exception {
        Integer updatedRowNumber = 1;
        final Single<Integer> returnedData = Single.just(updatedRowNumber);
        when(noteDao.updateNote(any(Note.class))).thenReturn(returnedData);
        //act
        Resource<Integer> returnedValue = noteRepository.updateNote(NOTE1).blockingFirst();

        verify(noteDao).updateNote(any(Note.class));
        verifyNoMoreInteractions(noteDao);

        assertEquals(Resource.success(1, UPDATE_SUCCESS), returnedValue);
    }

    @Test
    void updateRow_returnFail() throws Exception {
        Integer failedUpdateNumber = -1;
        final Single<Integer> returnedData = Single.just(failedUpdateNumber);
        when(noteDao.updateNote(any(Note.class))).thenReturn(returnedData);
        //act
        Resource<Integer> returnedValue = noteRepository.updateNote(NOTE1).blockingFirst();

        verify(noteDao).updateNote(any(Note.class));
        verifyNoMoreInteractions(noteDao);

        assertEquals(Resource.error(null, UPDATE_FAILURE), returnedValue);
    }

    @Test
    void updateRow_nullTitle_throwException() throws Exception{
        Exception exception = assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Note note = new Note(TEST_NOTE_1);
                note.setTitle(null);

                noteRepository.updateNote(note);
            }
        });

        assertEquals(NOTE_TITLE_NULL, exception.getMessage());
    }

    /*
        delete note
        null id
        throw exception
     */

    @Test
    void deleteNote_nullId_throwException() throws Exception {
        Exception exception = assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Note note = new Note(TestUtil.TEST_NOTE_1);
                note.setId(-1);
                noteRepository.deleteNote(note);
            }
        });

        assertEquals(INVALID_NOTE_ID, exception.getMessage());
    }

    /*
        delete note
        delete success
        return Resource.success with deleted row
     */

    @Test
    void deleteNote_deleteSuccess_returnResourceSuccess() throws Exception {
        // Arrange
        final int deletedRow = 1;
        Resource<Integer> successResponse = Resource.success(deletedRow, DELETE_SUCCESS);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        when(noteDao.deleteNote(any(Note.class))).thenReturn(Single.just(deletedRow));

        // Act
        Resource<Integer> observedResponse = liveDataTestUtil.getValue(noteRepository.deleteNote(NOTE1));

        // Assert
        assertEquals(successResponse, observedResponse);
    }


    /*
        delete note
        delete failure
        return Resource.error
     */
    @Test
    void deleteNote_deleteFailure_returnResourceError() throws Exception {
        // Arrange
        final int deletedRow = -1;
        Resource<Integer> errorResponse = Resource.error(null, DELETE_FAILURE);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        when(noteDao.deleteNote(any(Note.class))).thenReturn(Single.just(deletedRow));

        // Act
        Resource<Integer> observedResponse = liveDataTestUtil.getValue(noteRepository.deleteNote(NOTE1));

        // Assert
        assertEquals(errorResponse, observedResponse);
    }


    /*
        retrieve notes
        return list of notes
     */

    @Test
    void getNotes_returnListWithNotes() throws Exception {
        // Arrange
        List<Note> notes = TestUtil.TEST_NOTES_LIST;
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        MutableLiveData<List<Note>> returnedData = new MutableLiveData<>();
        returnedData.setValue(notes);
        when(noteDao.getNotes()).thenReturn(returnedData);

        // Act
        List<Note> observedData = liveDataTestUtil.getValue(noteRepository.getNotes());

        // Assert
        assertEquals(notes, observedData);
    }

    /*
        retrieve notes
        return empty list
     */

    @Test
    void getNotes_returnEmptyList() throws Exception {
        // Arrange
        List<Note> notes = new ArrayList<>();
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        MutableLiveData<List<Note>> returnedData = new MutableLiveData<>();
        returnedData.setValue(notes);
        when(noteDao.getNotes()).thenReturn(returnedData);

        // Act
        List<Note> observedData = liveDataTestUtil.getValue(noteRepository.getNotes());

        // Assert
        assertEquals(notes, observedData);
    }
}
