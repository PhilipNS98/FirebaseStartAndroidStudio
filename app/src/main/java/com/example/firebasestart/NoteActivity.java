package com.example.firebasestart;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;

public class NoteActivity extends AppCompatActivity {

    private FirebaseService firebaseService;
    private TextView noteTextView;

    private ImageView imageView;
    private ActivityResultLauncher<String> pickImageLauncher;
    private static final String IMAGE_TYPE = "image/*";
    private Uri selectedImageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        firebaseService = new FirebaseService();
        noteTextView = findViewById(R.id.noteTextView);
        imageView = findViewById(R.id.imageView);

        // Get the note ID from the intent's extras
        String noteId = getIntent().getStringExtra("noteId");

        // Retrieve the note details from the database and display them in the layout
        firebaseService.getNoteById(noteId, note -> {
            noteTextView.setText(note.getText());

            // If the note has an image URL, download and display the image
            if (note.getImageUrl() != null) {
                System.out.println(note.getImageUrl());

                // Load and display the image using Picasso
                Picasso.get().load(note.getImageUrl()).into(imageView);
            }
        });

        Button selectImageButton = findViewById(R.id.selectImageButton);
        // Create a new ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        imageView.setImageURI(result);
                        selectedImageUri = result;
                    }
                });
        selectImageButton.setOnClickListener(view -> {
            // Start the ActivityResultLauncher
            pickImageLauncher.launch(IMAGE_TYPE);
        });


        // Set up the save button to upload the selected image
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            // Get the image URI from the ImageView
            if (selectedImageUri != null) {
                // Call the FirebaseService method to upload the image
                firebaseService.uploadImage(noteId, selectedImageUri);
            }
        });
    }

    public void onBackButtonClicked(View view) {
        finish();
    }
}

