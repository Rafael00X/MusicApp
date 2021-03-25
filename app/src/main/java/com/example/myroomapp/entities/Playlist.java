package com.example.myroomapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Playlist {
    @NonNull
    @PrimaryKey
    private final String playlistName;

    public Playlist(@NonNull String playlistName) {
        this.playlistName = playlistName;
    }

    @NonNull
    public String getPlaylistName() {
        return playlistName;
    }
}