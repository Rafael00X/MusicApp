package com.example.myroomapp.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.relations.ArtistWithSongs;

import java.util.List;

@Dao
public interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Artist artist);

    @Delete
    void delete(Artist artist);

    @Transaction
    @Query("SELECT * FROM artist WHERE artistName = :artistName")
    List<ArtistWithSongs> getArtistWithSongs(String artistName);

    @Transaction
    @Query("SELECT * FROM artist")
    List<Artist> getAllArtists();
}
