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
import com.example.myroomapp.entities.relations.PlaylistSongCrossRef;

@Database(entities = {
            Song.class,
            Artist.class,
            Album.class,
            Playlist.class,
            PlaylistSongCrossRef.class
        },
        version = 1)

public abstract class MusicDatabase extends RoomDatabase {
    abstract AlbumDao albumDao();
    abstract ArtistDao artistDao();
    abstract SongDao songDao();
    abstract PlaylistDao playlistDao();
    abstract PlaylistSongCrossRefDao playlistSongCrossRefDao();

    private static MusicDatabase musicDatabase = null;

    public synchronized static MusicDatabase getInstance(Context context) {
        if (musicDatabase == null) {
            musicDatabase = Room.databaseBuilder(context.getApplicationContext(), MusicDatabase.class, "music_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return musicDatabase;
    }
}
