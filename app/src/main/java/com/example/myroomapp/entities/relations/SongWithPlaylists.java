package com.example.myroomapp.entities.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.Song;

import java.util.List;

public class SongWithPlaylists {
    @Embedded
    public Song song;

    @Relation(
            parentColumn = "songID",
            entityColumn = "playlistName",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )

    public List<Playlist> playlists;
}
