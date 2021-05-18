package com.example.musicplayer;

import android.content.Context;
import android.util.Log;

import com.example.musicplayer.daos.AlbumDao;
import com.example.musicplayer.daos.ArtistDao;
import com.example.musicplayer.daos.PlaylistDao;
import com.example.musicplayer.daos.PlaylistSongCrossRefDao;
import com.example.musicplayer.daos.SongDao;
import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.Artist;
import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.PlaylistSongCrossRef;
import com.example.musicplayer.entities.Song;
import com.example.musicplayer.entities.relations.AlbumWithSongs;
import com.example.musicplayer.entities.relations.ArtistWithSongs;
import com.example.musicplayer.entities.relations.PlaylistWithSongs;
import com.example.musicplayer.entities.relations.SongWithPlaylists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MusicRepository {
    private static MusicDatabase db;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MusicRepository(Context context) {
        db = MusicDatabase.getInstance(context);
    }

    public ArrayList<Song> getAllSongs() {
        ArrayList<Song> allSongs = new ArrayList<>();
        List<Song> songs = fetchAllSongsFromDB(db.songDao());
        if (songs != null) {
            allSongs.addAll(songs);
        }
        return allSongs;
    }

    public ArrayList<Album> getAllAlbums() {
        ArrayList<Album> allAlbums = new ArrayList<>();
        List<Album> albums = fetchAllAlbumsFromDB(db.albumDao());
        if (albums != null) {
            allAlbums.addAll(albums);
        }
        return allAlbums;
    }

    public ArrayList<Artist> getAllArtists() {
        ArrayList<Artist> allArtists = new ArrayList<>();
        List<Artist> artists = fetchAllArtistsFromDB(db.artistDao());
        if (artists != null) {
            allArtists.addAll(artists);
        }
        return allArtists;
    }

    public ArrayList<Playlist> getAllPlaylists() {
        ArrayList<Playlist> allPlaylists = new ArrayList<>();
        List<Playlist> playlists = fetchAllPlaylistsFromDB(db.playlistDao());
        if (playlists != null) {
            allPlaylists.addAll(playlists);
        }
        return allPlaylists;
    }

    public Album getAlbum(String albumName) {
        return fetchAlbumFromDB(db.albumDao(), albumName);
    }

    public Artist getArtist(String artistName) {
        return fetchArtistFromDB(db.artistDao(), artistName);
    }

    public Playlist getPlaylist(int playlistID) {
        return fetchPlaylistFromDB(db.playlistDao(), playlistID);
    }
    public Playlist getPlaylist(String playlistName) {
        return fetchPlaylistFromDB(db.playlistDao(), playlistName);
    }

    public ArrayList<Song> getSongsOfAlbum(Album album) {
        ArrayList<Song> songsOfAlbum = new ArrayList<>();
        List<Song> songs = fetchSongsOfAlbumFromDB(db.albumDao(), album);
        if (songs != null) {
            songsOfAlbum.addAll(songs);
        }
        return songsOfAlbum;
    }

    public ArrayList<Song> getSongsOfArtist(Artist artist) {
        ArrayList<Song> songsOfArtist = new ArrayList<>();
        List<Song> songs = fetchSongsOfArtistFromDB(db.artistDao(), artist);
        if (songs != null) {
            songsOfArtist.addAll(songs);
        }
        return songsOfArtist;
    }

    public ArrayList<Song> getSongsOfPlaylist(Playlist playlist) {
        ArrayList<Song> songsOfPlaylist = new ArrayList<>();
        List<Song> songs = fetchSongsOfPlaylistFromDB(db.playlistDao(), playlist);
        if (songs != null) {
            songsOfPlaylist.addAll(songs);
        }
        return songsOfPlaylist;
    }

    public ArrayList<Playlist> getPlaylistsOfSong(Song song) {
        ArrayList<Playlist> playlistsOfSong = new ArrayList<>();
        List<Playlist> playlists = fetchPlaylistsOfSongFromDB(db.songDao(), song);
        if (playlists != null) {
            playlistsOfSong.addAll(playlists);
        }
        return playlistsOfSong;
    }

    public void updateAlbum(Album album) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.albumDao().update(album);
            }
        });
    }

    public void updateArtist(Artist artist) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.artistDao().update(artist);
            }
        });
    }

    public void updatePlaylist(Playlist playlist) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.playlistDao().update(playlist);
            }
        });
    }

    public void insertSongs(ArrayList<Song> songs) {
        insertSongsToDB(db.songDao(), songs);
    }

    public void insertAlbums(ArrayList<Album> albums) {
        insertAlbumsToDB(db.albumDao(), albums);
    }

    public void insertArtists(ArrayList<Artist> artists) {
        insertArtistsToDB(db.artistDao(), artists);
    }

    public void insertPlaylist(Playlist playlist) {
        insertPlaylistToDB(db.playlistDao(), playlist);
    }

    public void insertSongsOfPlaylist(ArrayList<Song> songs, Playlist playlist) {
        insertSongsOfPlaylistToDB(db.playlistSongCrossRefDao(), songs, playlist);
    }

    public void insertPlaylistsOfSong(Song song, ArrayList<Playlist> playlists) {
        insertPlaylistsOfSongToDB(db.playlistSongCrossRefDao(), song, playlists);
    }

    public void deleteSong(Song song) {
        deletePlaylistsOfSongFromDB(db.playlistSongCrossRefDao(), song);
        deleteSongFromDB(db.songDao(), song);

        Album album = getAlbum(song.getSongAlbum());
        Artist artist = getArtist(song.getSongArtist());

        album.songCount--;
        if (album.songCount == 0) {
            deleteAlbumFromDB(db.albumDao(), album);
        }
        else {
            insertAlbumToDB(db.albumDao(), album);
        }

        artist.songCount--;
        if (artist.songCount == 0) {
            deleteArtistFromDB(db.artistDao(), artist);
        }
        else {
            insertArtistToDB(db.artistDao(), artist);
        }
    }

    public void deletePlaylist(Playlist playlist) {
        deleteSongsOfPlaylistFromDB(db.playlistSongCrossRefDao(), playlist);
        deletePlaylistFromDB(db.playlistDao(), playlist);
    }

    public void deleteSongFromPlaylist(Song song, Playlist playlist) {
        deleteSongFromPlaylistFromDB(db.playlistSongCrossRefDao(), song, playlist);
    }




    private List<Song> fetchAllSongsFromDB(SongDao songDao) {
        Future<List<Song>> future = executor.submit(new Callable<List<Song>>() {
            @Override
            public List<Song> call() {
                return songDao.getAllSongs();
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getAllSongs: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getAllSongs: InterruptedException");
        }
        return null;
    }

    private List<Album> fetchAllAlbumsFromDB(AlbumDao albumDao) {
        Future<List<Album>> future = executor.submit(new Callable<List<Album>>() {
            @Override
            public List<Album> call() {
                return albumDao.getAllAlbums();
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getAllAlbums: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getAllAlbums: InterruptedException");
        }
        return null;
    }

    private List<Artist> fetchAllArtistsFromDB(ArtistDao artistDao) {
        Future<List<Artist>> future = executor.submit(new Callable<List<Artist>>() {
            @Override
            public List<Artist> call() {
                return artistDao.getAllArtists();
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getAllArtists: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getAllArtists: InterruptedException");
        }
        return null;
    }

    private List<Playlist> fetchAllPlaylistsFromDB(PlaylistDao playlistDao) {
        Future<List<Playlist>> future = executor.submit(new Callable<List<Playlist>>() {
            @Override
            public List<Playlist> call() {
                return playlistDao.getAllPlaylists();
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getAllPlaylists: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getAllPlaylists: InterruptedException");
        }
        return null;
    }

    private Album fetchAlbumFromDB(AlbumDao albumDao, String albumName) {
        Future<Album> future = executor.submit(new Callable<Album>() {
            @Override
            public Album call() {
                return albumDao.getAlbum(albumName);
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getAlbum: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getAlbum: InterruptedException");
        }
        return null;
    }

    private Artist fetchArtistFromDB(ArtistDao artistDao, String artistName) {
        Future<Artist> future = executor.submit(new Callable<Artist>() {
            @Override
            public Artist call() {
                return artistDao.getArtist(artistName);
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getArtist: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getArtist: InterruptedException");
        }
        return null;
    }

    private Playlist fetchPlaylistFromDB(PlaylistDao playlistDao, int playlistID) {
        Future<Playlist> future = executor.submit(new Callable<Playlist>() {
            @Override
            public Playlist call() {
                return playlistDao.getPlaylist(playlistID);
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getPlaylist: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getPlaylist: InterruptedException");
        }
        return null;
    }
    private Playlist fetchPlaylistFromDB(PlaylistDao playlistDao, String playlistName) {
        Future<Playlist> future = executor.submit(new Callable<Playlist>() {
            @Override
            public Playlist call() {
                return playlistDao.getPlaylist(playlistName);
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getPlaylist: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getPlaylist: InterruptedException");
        }
        return null;
    }

    private List<Song> fetchSongsOfAlbumFromDB(AlbumDao albumDao, Album album) {
        Future<List<Song>> future = executor.submit(new Callable<List<Song>>() {
            @Override
            public List<Song> call() {
                AlbumWithSongs albumWithSongs = albumDao.getAlbumWithSongs(album.getAlbumName()).get(0);
                return albumWithSongs.songs;
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getSongsOfAlbum: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getSongsOfAlbum: InterruptedException");
        }
        return null;
    }

    private List<Song> fetchSongsOfArtistFromDB(ArtistDao artistDao, Artist artist) {
        Future<List<Song>> future = executor.submit(new Callable<List<Song>>() {
            @Override
            public List<Song> call() {
                ArtistWithSongs artistWithSongs = artistDao.getArtistWithSongs(artist.getArtistName()).get(0);
                return artistWithSongs.songs;
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getSongsOfArtist: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getSongsOfArtist: InterruptedException");
        }
        return null;
    }

    private List<Song> fetchSongsOfPlaylistFromDB(PlaylistDao playlistDao, Playlist playlist) {
        Future<List<Song>> future = executor.submit(new Callable<List<Song>>() {
            @Override
            public List<Song> call() {
                PlaylistWithSongs playlistWithSongs = playlistDao.getPlaylistWithSongs(playlist.getPlaylistID()).get(0);
                return playlistWithSongs.songs;
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getSongsOfPlaylist: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getSongsOfPlaylist: InterruptedException");
        }
        return null;
    }

    private List<Playlist> fetchPlaylistsOfSongFromDB(SongDao songDao, Song song) {
        Future<List<Playlist>> future = executor.submit(new Callable<List<Playlist>>() {
            @Override
            public List<Playlist> call() {
                SongWithPlaylists songWithPlaylists = songDao.getSongWithPlaylists(song.getSongID()).get(0);
                return songWithPlaylists.playlists;
            }
        });
        try {
            return future.get();
        }
        catch (ExecutionException e) {
            Log.d("TAG", "getSongsOfPlaylist: ExecutionException");
        }
        catch (InterruptedException e) {
            Log.d("TAG", "getSongsOfPlaylist: InterruptedException");
        }
        return null;
    }

    private void insertSongsToDB(SongDao songDao, ArrayList<Song> songs) {
        executor.submit((Callable<Void>) () -> {
            for (Song song : songs)
                songDao.insert(song);
            return null;
        });
    }

    private void insertAlbumsToDB(AlbumDao albumDao, ArrayList<Album> albums) {
        executor.submit((Callable<Void>) () -> {
            Album temp_album;
            for (Album album : albums) {
                temp_album = albumDao.getAlbum(album.getAlbumName());
                if (temp_album != null)
                    album.songCount += temp_album.songCount;
                albumDao.insert(album);
            }
            return null;
        });
    }
    private void insertAlbumToDB(AlbumDao albumDao, Album album) {
        executor.submit((Callable<Void>) () -> {
            albumDao.insert(album);
            return null;
        });
    }

    private void insertArtistsToDB(ArtistDao artistDao, ArrayList<Artist> artists) {
        executor.submit((Callable<Void>) () -> {
            Artist temp_artist;
            for (Artist artist : artists) {
                temp_artist = artistDao.getArtist(artist.getArtistName());
                if (temp_artist != null)
                    artist.songCount += temp_artist.songCount;
                artistDao.insert(artist);
            }
            return null;
        });
    }
    private void insertArtistToDB(ArtistDao artistDao, Artist artist) {
        executor.submit((Callable<Void>) () -> {
            artistDao.insert(artist);
            return null;
        });
    }

    private void insertPlaylistToDB(PlaylistDao playlistDao, Playlist playlist) {
        executor.submit((Callable<Void>) () -> {
            playlistDao.insert(playlist);
            return null;
        });
    }

    private void insertSongsOfPlaylistToDB(PlaylistSongCrossRefDao playlistSongCrossRefDao, ArrayList<Song> songs, Playlist playlist) {
        executor.submit((Callable<Void>) () -> {
            for (int i = 0; i < songs.size(); i++) {
                playlistSongCrossRefDao.insert(new PlaylistSongCrossRef(songs.get(i).getSongID(), playlist.getPlaylistID()));
            }
            return null;
        });
    }

    private void insertPlaylistsOfSongToDB(PlaylistSongCrossRefDao playlistSongCrossRefDao, Song song, ArrayList<Playlist> playlists) {
        executor.submit((Callable<Void>) () -> {
            for (int i = 0; i < playlists.size(); i++) {
                playlistSongCrossRefDao.insert(new PlaylistSongCrossRef(song.getSongID(), playlists.get(i).getPlaylistID()));
                db.playlistDao().update(playlists.get(i));
            }
            return null;
        });
    }

    private void deleteSongFromDB(SongDao songDao, Song song) {
        executor.submit((Callable<Void>) () -> {
            songDao.delete(song);
            return null;
        });
    }

    private void deleteAlbumFromDB(AlbumDao albumDao, Album album) {
        executor.submit((Callable<Void>) () -> {
            albumDao.delete(album);
            return null;
        });
    }

    private void deleteArtistFromDB(ArtistDao artistDao, Artist artist) {
        executor.submit((Callable<Void>) () -> {
            artistDao.delete(artist);
            return null;
        });
    }

    private void deletePlaylistFromDB(PlaylistDao playlistDao, Playlist playlist) {
        executor.submit((Callable<Void>) () -> {
            playlistDao.delete(playlist);
            return null;
        });
    }

    private void deleteSongsOfPlaylistFromDB(PlaylistSongCrossRefDao playlistSongCrossRefDao, Playlist playlist) {
        executor.submit((Callable<Void>) () -> {
            playlistSongCrossRefDao.deletePlaylist(playlist.getPlaylistID());
            return null;
        });
    }

    private void deletePlaylistsOfSongFromDB(PlaylistSongCrossRefDao playlistSongCrossRefDao, Song song) {
        executor.submit((Callable<Void>) () -> {
            playlistSongCrossRefDao.deleteSong(song.getSongID());
            return null;
        });
    }

    private void deleteSongFromPlaylistFromDB(PlaylistSongCrossRefDao playlistSongCrossRefDao, Song song, Playlist playlist) {
        executor.submit((Callable<Void>) () -> {
            playlistSongCrossRefDao.deleteSongFromPlaylist(song.getSongID(), playlist.getPlaylistID());
            db.playlistDao().update(playlist);
            return null;
        });
    }

}
