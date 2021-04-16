package com.example.myroomapp.entities;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Album {
    @NonNull
    @PrimaryKey
    private final String albumName;
    public String imagePath;
    @Ignore
    public Bitmap image;

    public Album(@NonNull String albumName) {
        this.albumName = albumName;
    }

    @NonNull
    public String getAlbumName() {
        return albumName;
    }
}