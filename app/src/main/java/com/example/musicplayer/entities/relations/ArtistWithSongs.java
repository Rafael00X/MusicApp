package com.example.musicplayer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.musicplayer.entities.Artist;
import com.example.musicplayer.entities.Song;

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