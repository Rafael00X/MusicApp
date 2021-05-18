package com.example.musicplayer.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.musicplayer.entities.Song;
import com.example.musicplayer.entities.relations.SongWithPlaylists;

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
    List<SongWithPlaylists> getSongWithPlaylists(long songID);

    @Transaction
    @Query("SELECT * FROM song ORDER BY LOWER(songName) ASC")
    List<Song> getAllSongs();

}
