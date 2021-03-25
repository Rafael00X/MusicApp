package com.example.myroomapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Album {
    @NonNull
    @PrimaryKey
    private final String albumName;

    public Album(@NonNull String albumName) {
        this.albumName = albumName;
    }

    @NonNull
    public String getAlbumName() {
        return albumName;
    }
}