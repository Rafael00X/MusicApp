package com.example.myroomapp.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.myroomapp.entities.Song;
import com.example.myroomapp.entities.relations.PlaylistWithSongs;
import com.example.myroomapp.entities.relations.SongWithPlaylists;

import java.util.List;

@Dao
public interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Song song);

    @Delete
    void delete(Song song);

    @Query("DELETE FROM Song")
    void deleteAll();

    @Transaction
    @Query("SELECT * FROM song WHERE songID = :songID")
    List<SongWithPlaylists> getSongWithPlaylists(int songID);

}
