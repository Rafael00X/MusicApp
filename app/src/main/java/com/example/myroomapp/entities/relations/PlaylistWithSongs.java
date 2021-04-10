package com.example.myroomapp.entities.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.PlaylistSongCrossRef;
import com.example.myroomapp.entities.Song;

import java.util.List;

public class PlaylistWithSongs {
    @Embedded
    public Playlist playlist;

    @Relation(
            parentColumn = "playlistID",
            entityColumn = "songID",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )

    public List<Song> songs;
}
