package com.example.myroomapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myroomapp.daos.AlbumDao;
import com.example.myroomapp.daos.ArtistDao;
import com.example.myroomapp.daos.PlaylistDao;
import com.example.myroomapp.daos.PlaylistSongCrossRefDao;
import com.example.myroomapp.daos.SongDao;
import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.Song;
import com.example.myroomapp.entities.PlaylistSongCrossRef;

@Database(entities = {
            Song.class,
            Artist.class,
            Album.class,
            Playlist.class,
            PlaylistSongCrossRef.class
        },
        version = 1,
        exportSchema = false)

public abstract class MusicDatabase extends RoomDatabase {
    public abstract AlbumDao albumDao();
    public abstract ArtistDao artistDao();
    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
    public abstract PlaylistSongCrossRefDao playlistSongCrossRefDao();

    private static MusicDatabase musicDatabase = null;

    public synchronized static MusicDatabase getInstance(Context context) {
        if (musicDatabase == null) {
            musicDatabase = Room.databaseBuilder(context.getApplicationContext(), MusicDatabase.class, "music_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return musicDatabase;
    }
}
