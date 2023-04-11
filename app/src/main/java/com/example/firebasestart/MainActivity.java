package com.example.firebasestart;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.firebasestart.model.Note;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseService firebaseService;
    private ImageView imageView;
    private ActivityResultLauncher<String> pickImageLauncher;
    private static final String IMAGE_TYPE = "image/*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        firebaseService = new FirebaseService();
        imageView = findViewById(R.id.imageView);
        //firebaseService.add2Note("hi from Android, with feedback");
        showNotes();

        Button selectImageButton = findViewById(R.id.selectImageButton);
        // Create a new ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        imageView.setImageURI(result);
                        /*firebaseService.uploadImage(result);*/
                    }
                });
        selectImageButton.setOnClickListener(view -> {
            // Start the ActivityResultLauncher
            pickImageLauncher.launch(IMAGE_TYPE);
        });

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

            // Set a click listener for each item in the ListView
            listView.setOnItemClickListener((adapterView, view, position, id) -> {
                // Get the selected note ID
                Note selectedNote = (Note) adapterView.getItemAtPosition(position);
                String noteId = selectedNote.getId();

                // Start the NoteActivity and pass the note ID as an extra
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("noteId", noteId);
                startActivity(intent);
            });

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
                    String imageUrl = document.getString("imageUrl");
                    String id = document.getId();
                    Note note = new Note(text, id, imageUrl);
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