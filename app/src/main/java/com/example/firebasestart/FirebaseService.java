package com.example.firebasestart;

import com.example.firebasestart.model.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                String id = document.getId();
                Note note = new Note(text, id);
                notes.add(note);
            }
            consumer.accept(notes);
        });
    }




}
