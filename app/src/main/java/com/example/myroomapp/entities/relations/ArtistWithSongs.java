package com.example.myroomapp.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Song;

import java.util.List;

public class ArtistWithSongs {
    @Embedded
    public Artist artist;

    @Relation(
            parentColumn = "artistName",
            entityColumn = "songArtist"
    )

    public List<Song> songs;
}