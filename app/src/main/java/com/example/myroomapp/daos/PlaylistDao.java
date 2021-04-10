package com.example.myroomapp.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.relations.AlbumWithSongs;
import com.example.myroomapp.entities.relations.PlaylistWithSongs;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Playlist playlist);

    @Delete
    void delete(Playlist playlist);

    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistID = :playlistID")
    List<PlaylistWithSongs> getPlaylistWithSongs(int playlistID);

    @Transaction
    @Query("SELECT * FROM playlist")
    List<Playlist> getAllPlaylists();
}