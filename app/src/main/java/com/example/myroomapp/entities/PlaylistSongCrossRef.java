package com.example.myroomapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"songID", "playlistID"})
public class PlaylistSongCrossRef {
    private final long songID;
    private final int playlistID;

    public PlaylistSongCrossRef(long songID, int playlistID) {
        this.songID = songID;
        this.playlistID = playlistID;
    }

    public long getSongID() {
        return songID;
    }

    public int getPlaylistID() {
        return playlistID;
    }
}
