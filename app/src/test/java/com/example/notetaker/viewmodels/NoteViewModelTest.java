package com.example.notetaker.viewmodels;

import com.example.notetaker.models.Note;
import com.example.notetaker.repository.NoteRepository;
import com.example.notetaker.ui.Resource;
import com.example.notetaker.util.InstantExecutorExtension;
import com.example.notetaker.util.LiveDataTestUtil;
import com.example.notetaker.util.TestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Flowable;
import io.reactivex.internal.operators.single.SingleToFlowable;

import static com.example.notetaker.repository.NoteRepository.INSERT_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//the InstantExecutorExtension class is the JUnit5 equivalent for forcing operations to run on the main thread
@ExtendWith(InstantExecutorExtension.class)
public class NoteViewModelTest {

    private NoteViewModel noteViewModel;

    @Mock
    private NoteRepository noteRepository;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
        noteViewModel = new NoteViewModel(noteRepository);
    }

    //cant observe note that hasn't been set

    @Test
    void observeNote_whenNotSet() throws Exception{
        //Arrange
        LiveDataTestUtil<Note> liveDataTestUtil = new LiveDataTestUtil<>();
        //Act
        Note note = liveDataTestUtil.getValue(noteViewModel.observeNote());
        //Assert
        assertNull(note);
    }

    //observe a note that has been set

    @Test
    void observeNote_whenSet() throws Exception{
        //Arrange
        Note note = new Note(TestUtil.TEST_NOTE_1);
        LiveDataTestUtil<Note> liveDataTestUtil = new LiveDataTestUtil<>();
        //Act
        noteViewModel.setNote(note);
        Note observedNote = liveDataTestUtil.getValue(noteViewModel.observeNote());
        //Assert
        assertEquals(observedNote, note);
    }

    //insert a new note and observe the row returned

    @Test
    void insertNote_returnRow() throws Exception {
        //Arrange
        Note note = new Note(TestUtil.TEST_NOTE_1);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        final int insertedRow = 1;
        Flowable<Resource<Integer>> returnedData = SingleToFlowable.just(Resource.success(insertedRow, INSERT_SUCCESS));
        when(noteRepository.insertNote(any(Note.class))).thenReturn(returnedData);
        //Act
        noteViewModel.setNote(note);
        Resource<Integer> returnedValue = liveDataTestUtil.getValue(noteViewModel.insertNote());
        //Assert
        assertEquals(Resource.success(insertedRow, INSERT_SUCCESS), returnedValue);
    }


    //insert, but don't return a new row without an observer

    @Test
    void dontReturnInsertRow_withoutObserver() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);

        noteViewModel.setNote(note);

        verify(noteRepository, never()).insertNote(any(Note.class));
    }

    //set note, null title, throw exception

    @Test
    void setNote_NullTitle_throwException() throws Exception {
        final Note note = new Note(TestUtil.TEST_NOTE_1);
        note.setTitle(null);

        assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                noteViewModel.setNote(note);
            }
        });
    }

}
