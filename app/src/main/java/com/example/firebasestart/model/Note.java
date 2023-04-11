package com.example.firebasestart.model;

public class Note {
    private String text;
    private String id;
    private String imageUrl;

    public Note(String text, String id, String imageUrl) {
        this.text = text;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return text;
    }
}
