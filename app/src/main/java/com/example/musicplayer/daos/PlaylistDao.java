package com.example.musicplayer.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.relations.PlaylistWithSongs;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Playlist playlist);

    @Delete
    void delete(Playlist playlist);

    @Query("SELECT * FROM playlist WHERE playlistID = :playlistID")
    Playlist getPlaylist(int playlistID);

    @Query("SELECT * FROM playlist WHERE playlistName = :playlistName")
    Playlist getPlaylist(String playlistName);

    @Update
    void update(Playlist playlist);

    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistID = :playlistID")
    List<PlaylistWithSongs> getPlaylistWithSongs(int playlistID);

    @Transaction
    @Query("SELECT * FROM playlist")
    List<Playlist> getAllPlaylists();
}