package com.example.firebasestart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.firebasestart.model.Note;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseService firebaseService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseService = new FirebaseService();
        //firebaseService.add2Note("hi from Android, with feedback");
        showNotes();
    }

    public void showNotes() {
        // Retrieve all the notes from the Firestore database
        firebaseService.getAllNotes(notes -> {
            // Create an adapter to display the notes in a ListView
            ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(
                    this,
                    android.R.layout.simple_list_item_1,
                    notes
            );
            // Set the adapter to the ListView
            ListView listView = findViewById(R.id.notesListView);
            listView.setAdapter(adapter);

            // Get an instance of the firebase Database.
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Add a listener to the Firestore database to update the adapter when a new document is added
            db.collection("notes2").addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    // handle errors here
                    return;
                }
                List<Note> newNotes = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String text = document.getString("text");
                    String id = document.getId();
                    Note note = new Note(text, id);
                    newNotes.add(note);
                }
                adapter.clear();
                adapter.addAll(newNotes);
                adapter.notifyDataSetChanged();
            });
        });
    }

    //TODO sort the ListView alphabetically
    //TODO make add delete and edit buttons



}