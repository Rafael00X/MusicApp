package com.example.myroomapp.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.relations.AlbumWithSongs;

import java.util.List;

@Dao
public interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Album album);

    @Delete
    void delete(Album album);

    @Transaction
    @Query("SELECT * FROM album WHERE albumName = :albumName")
    List<AlbumWithSongs> getAlbumWithSongs(String albumName);
}
