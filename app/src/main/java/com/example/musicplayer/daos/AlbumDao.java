package com.example.musicplayer.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.relations.AlbumWithSongs;

import java.util.List;

@Dao
public interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Album album);

    @Delete
    void delete(Album album);

    @Query("SELECT * FROM album WHERE albumName = :albumName")
    Album getAlbum(String albumName);

    @Update
    void update(Album album);

    @Transaction
    @Query("SELECT * FROM album WHERE albumName = :albumName")
    List<AlbumWithSongs> getAlbumWithSongs(String albumName);

    @Transaction
    @Query("SELECT * FROM album ORDER BY LOWER(albumName) ASC")
    List<Album> getAllAlbums();
}
