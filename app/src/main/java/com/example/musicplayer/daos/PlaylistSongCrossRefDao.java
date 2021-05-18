package com.example.musicplayer.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.musicplayer.entities.PlaylistSongCrossRef;

@Dao
public interface PlaylistSongCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlaylistSongCrossRef playlistSongCrossRef);

    @Delete
    void delete(PlaylistSongCrossRef playlistSongCrossRef);

    @Query("DELETE FROM playlistsongcrossref WHERE playlistID = :playlistID")
    void deletePlaylist(int playlistID);

    @Query("DELETE FROM playlistsongcrossref WHERE songID = :songID")
    void deleteSong(long songID);

    @Query("DELETE FROM playlistsongcrossref WHERE songID = :songID AND playlistID = :playlistID")
    void deleteSongFromPlaylist(long songID, int playlistID);
}
