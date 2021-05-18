package com.example.musicplayer.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.musicplayer.entities.Artist;
import com.example.musicplayer.entities.relations.ArtistWithSongs;

import java.util.List;

@Dao
public interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Artist artist);

    @Delete
    void delete(Artist artist);

    @Query("SELECT * FROM artist WHERE artistName = :artistName")
    Artist getArtist(String artistName);

    @Update
    void update(Artist artist);

    @Transaction
    @Query("SELECT * FROM artist WHERE artistName = :artistName")
    List<ArtistWithSongs> getArtistWithSongs(String artistName);

    @Transaction
    @Query("SELECT * FROM artist ORDER BY LOWER(artistName) ASC")
    List<Artist> getAllArtists();
}
