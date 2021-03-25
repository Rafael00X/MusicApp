package com.example.myroomapp.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Song;

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
