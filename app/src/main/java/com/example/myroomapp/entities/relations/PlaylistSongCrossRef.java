package com.example.myroomapp.entities.relations;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"songID", "playlistName"})
public class PlaylistSongCrossRef {
    private final int songID;
    @NonNull
    private final String playlistName;

    public PlaylistSongCrossRef(int songID, @NonNull String playlistName) {
        this.songID = songID;
        this.playlistName = playlistName;
    }

    public int getSongID() {
        return songID;
    }

    public String getPlaylistName() {
        return playlistName;
    }
}
