package com.example.musicplayer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.PlaylistSongCrossRef;
import com.example.musicplayer.entities.Song;

import java.util.List;

public class SongWithPlaylists {
    @Embedded
    public Song song;

    @Relation(
            parentColumn = "songID",
            entityColumn = "playlistID",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )

    public List<Playlist> playlists;
}
