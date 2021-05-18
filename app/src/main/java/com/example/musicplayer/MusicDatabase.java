package com.example.musicplayer;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.musicplayer.daos.AlbumDao;
import com.example.musicplayer.daos.ArtistDao;
import com.example.musicplayer.daos.PlaylistDao;
import com.example.musicplayer.daos.PlaylistSongCrossRefDao;
import com.example.musicplayer.daos.SongDao;
import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.Artist;
import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.Song;
import com.example.musicplayer.entities.PlaylistSongCrossRef;

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
