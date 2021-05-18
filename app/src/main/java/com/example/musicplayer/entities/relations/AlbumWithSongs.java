package com.example.musicplayer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.Song;

import java.util.List;

public class AlbumWithSongs {
    @Embedded
    public Album album;

    @Relation(
            parentColumn = "albumName",
            entityColumn = "songAlbum"
    )

    public List<Song> songs;
}
