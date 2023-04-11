package com.example.firebasestart;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.firebasestart.model.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class FirebaseService {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addNote(String text) {
        DocumentReference ref = db.collection("notes2").document();
        Map<String, String> map = new HashMap<>();
        map.put("text", text);
        ref.set(map);
    }
    public void add2Note(String text){
        DocumentReference ref = db.collection("notes2").document();
        Map<String, String> map = new HashMap<>();
        map.put("text", text);
        ref.set(map).addOnSuccessListener(unused ->
                System.out.println("document saved, " + text))
                .addOnFailureListener( e ->
                        System.out.println("documnet NOT saved, " + text));
    }

    public void getAllNotes(Consumer<List<Note>> consumer) {
        db.collection("notes2").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Note> notes = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String text = document.getString("text");
                String imageUrl = document.getString("imageUrl");
                if(imageUrl == null){
                    imageUrl = "";

                }
                String id = document.getId();
                Note note = new Note(text, id, imageUrl);
                notes.add(note);
            }
            consumer.accept(notes);
        });
    }

    public void getNoteById(String noteId, Consumer<Note> consumer) {
        db.collection("notes2").document(noteId).get().addOnSuccessListener(documentSnapshot -> {
            String text = documentSnapshot.getString("text");
            String imageUrl = documentSnapshot.getString("imageUrl");
            Note note = new Note(text, noteId, imageUrl);
            consumer.accept(note);
        });
    }

    public void uploadImage(String noteId, Uri imageUri) {
        // Create a storage reference with a unique name for the image
        String imageName = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(imageName);

        // Upload the image to Cloud Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                    updateNoteImageUrl(noteId, uri.toString());

                            }).addOnFailureListener(e -> {
                                    e.printStackTrace();
                            });
                    // Image uploaded successfully
                   //Toast.makeText(NoteActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error uploading image
                    Log.e("FirebaseService", "Error uploading image", e);
                    //Toast.makeText(NoteActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                });
    }

    public void updateNoteImageUrl(String noteId, String imageUrl) {
        DocumentReference noteRef = db.collection("notes2").document(noteId);
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        noteRef.set(data, SetOptions.merge());
    }




}
