package com.example.myroomapp.entities;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Artist {
    @NonNull
    @PrimaryKey
    private final String artistName;
    public String imagePath;
    @Ignore
    public Bitmap image;

    public Artist(@NonNull String artistName) {
        this.artistName = artistName;
    }

    @NonNull
    public String getArtistName() {
        return artistName;
    }
}
