package com.example.myroomapp;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.Song;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ItemViewModel extends AndroidViewModel {
    private final MusicRepository repository;
    public static final ArrayList<Song> allSongs = new ArrayList<>();
    public static final ArrayList<Album> allAlbums = new ArrayList<>();
    public static final ArrayList<Artist> allArtists = new ArrayList<>();
    public static final ArrayList<Playlist> allPlaylists = new ArrayList<>();
    //private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public ItemViewModel(@NonNull Application application) {
        super(application);
        repository = new MusicRepository(application);
    }

    public void loadData() {
        allSongs.clear();
        allAlbums.clear();
        allArtists.clear();
        allPlaylists.clear();

        allSongs.addAll(repository.getAllSongs());
        setSongImages(allSongs);
        allAlbums.addAll(repository.getAllAlbums());
        setAlbumImages(allAlbums);
        allArtists.addAll(repository.getAllArtists());
        setArtistImages(allArtists);
        allPlaylists.addAll(repository.getAllPlaylists());
        //setPlaylistImages(allPlaylists);
    }

    public ArrayList<Song> getAllSongs() {
        return allSongs;
    }

    public ArrayList<Album> getAllAlbums() {
        return allAlbums;
    }

    public ArrayList<Artist> getAllArtists() {
        return allArtists;
    }

    public ArrayList<Playlist> getAllPlaylists() {
        return allPlaylists;
    }

    public ArrayList<Song> getSongsOfAlbum(Album album) {
        ArrayList<Song> songs = repository.getSongsOfAlbum(album);
        setSongImages(songs);
        return songs;
    }

    public ArrayList<Song> getSongsOfArtist(Artist artist) {
        ArrayList<Song> songs = repository.getSongsOfArtist(artist);
        setSongImages(songs);
        return songs;
    }

    public ArrayList<Song> getSongsOfPlaylist(Playlist playlist) {
        ArrayList<Song> songs = repository.getSongsOfPlaylist(playlist);
        setSongImages(songs);
        return songs;
    }

    public ArrayList<Playlist> getPlaylistsOfSong(Song song) {
        return repository.getPlaylistsOfSong(song);
    }


    public void insertSongs(ArrayList<Song> songs) {
        repository.insertSongs(songs);
    }

    public void insertAlbums(ArrayList<Album> albums) {
        repository.insertAlbums(albums);
    }

    public void insertArtists(ArrayList<Artist> artists) {
        repository.insertArtists(artists);
    }


    public void insertPlaylist(Playlist playlist) {
        repository.insertPlaylist(playlist);
    }

    public void insertSongsOfPlaylist(ArrayList<Song> songs, Playlist playlist) {
        repository.insertSongsOfPlaylist(songs, playlist);
    }

    public void insertPlaylistsOfSong(Song song, ArrayList<Playlist> playlists) {
        repository.insertPlaylistsOfSong(song, playlists);
    }

    public void deleteSong(Song song) {
        //repository.deleteSong(song);
        ArrayList<Boolean> b = repository.deleteSong(song);
        if (b.get(0)) {
            for (int i = 0; i < allAlbums.size(); i++) {
                if (allAlbums.get(i).getAlbumName().equals(song.getSongAlbum())) {
                    allAlbums.remove(i);
                    break;
                }
            }
        }
        if (b.get(1)) {
            for (int i = 0; i < allArtists.size(); i++) {
                if (allArtists.get(i).getArtistName().equals(song.getSongArtist())) {
                    allArtists.remove(i);
                    break;
                }
            }
        }
    }

    public void deletePlaylist(Playlist playlist) {
        repository.deletePlaylist(playlist);
    }

    public void deleteSongFromPlaylist(Song song, Playlist playlist) {
        repository.deleteSongFromPlaylist(song, playlist);
    }


    public ArrayList<Song> fetchSongsFromMediaStore(Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
        String image_dir = "content://media/external/audio/albumart";

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String lastModified = Long.toString(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));
                String path = uri.toString() + File.separator + id;
                String image_path = image_dir + File.separator + album_id;

                Song song = new Song(id, name, album, artist, lastModified, path);
                song.imagePath = image_path;

                songs.add(song);
            }
            cursor.close();
        }
        return songs;
    }

    public void setSongImages(ArrayList<Song> songs) {
        Bitmap bitmap;
        for (int i = 0; i < songs.size(); i++) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), Uri.parse(songs.get(i).imagePath));
            } catch (Exception e) {
                bitmap = null;
            }
            songs.get(i).image = bitmap;
        }
    }

    public void setAlbumImages(ArrayList<Album> albums) {
        Bitmap bitmap;
        for (int i = 0; i < albums.size(); i++) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), Uri.parse(albums.get(i).imagePath));
            } catch (Exception e) {
                bitmap = null;
            }
            albums.get(i).image = bitmap;
        }
    }

    public void setArtistImages(ArrayList<Artist> artists) {
        Bitmap bitmap;
        for (int i = 0; i < artists.size(); i++) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), Uri.parse(artists.get(i).imagePath));
            } catch (Exception e) {
                bitmap = null;
            }
            artists.get(i).image = bitmap;
        }
    }

    private final MutableLiveData<String> loadFragment = new MutableLiveData<>();
    public void setLoadFragment(String loadFragment) {
        this.loadFragment.setValue(loadFragment);
    }
    public LiveData<String> getLoadFragment() {
        return loadFragment;
    }

    private final MutableLiveData<String> playerTask = new MutableLiveData<>();
    public void setPlayerTask(String playerTask) {
        this.playerTask.setValue(playerTask);
    }
    public LiveData<String> getPlayerTask() {
        return playerTask;
    }

    private final MutableLiveData<String> otherEvents = new MutableLiveData<>();
    public void setOtherEvents(String otherEvents) {
        this.otherEvents.setValue(otherEvents);
    }
    public LiveData<String> getOtherEvents() {
        return otherEvents;
    }


    private ArrayList<Song> queue = null;
    public void setQueue(ArrayList<Song> queue) {
        this.queue = queue;
    }
    public ArrayList<Song> getQueue() {
        return queue;
    }

    private int songPosition;
    public void setSongPosition(int songPosition) {
        this.songPosition = songPosition;
    }
    public int getSongPosition() {
        return songPosition;
    }

    private Song currentSong = null;
    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }
    public Song getCurrentSong() {
        return currentSong;
    }

    private Album album = null;
    public void setAlbum(Album album) {
        this.album = album;
    }
    public Album getAlbum() {
        return album;
    }

    private Artist artist = null;
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public Artist getArtist() {
        return artist;
    }

    private Playlist playlist = null;
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
    public Playlist getPlaylist() {
        return playlist;
    }

    public boolean mediaIsPlaying = false;
}



