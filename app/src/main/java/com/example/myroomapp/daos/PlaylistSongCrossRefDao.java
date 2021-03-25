package com.example.myroomapp.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.relations.PlaylistSongCrossRef;

@Dao
public interface PlaylistSongCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlaylistSongCrossRef playlistSongCrossRef);

    @Delete
    void delete(PlaylistSongCrossRef playlistSongCrossRef);
}
