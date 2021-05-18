package com.example.musicplayer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.PlaylistSongCrossRef;
import com.example.musicplayer.entities.Song;

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
