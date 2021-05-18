package com.example.musicplayer;

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

import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.Artist;
import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.Song;

import java.io.File;
import java.util.ArrayList;


public class ItemViewModel extends AndroidViewModel {
    private final MusicRepository repository;
    public static final ArrayList<Song> allSongs = new ArrayList<>();
    public static final ArrayList<Album> allAlbums = new ArrayList<>();
    public static final ArrayList<Artist> allArtists = new ArrayList<>();
    public static final ArrayList<Playlist> allPlaylists = new ArrayList<>();
    public Song current_song;
    private static final String TAG = "IVM**********";

    /*
    private final MutableLiveData<PlayerService.PlayerBinder> playerBinder = new MutableLiveData<>();
    public void setPlayerBinder(PlayerService.PlayerBinder binder) {
        playerBinder.setValue(binder);
    }
    public LiveData<PlayerService.PlayerBinder> getPlayerBinder() {
        return playerBinder;
    }
    public final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            playerBinder.postValue((PlayerService.PlayerBinder) service);
        }

        public void onServiceDisconnected(ComponentName className) {
            playerBinder.postValue(null);
        }
    };
    private boolean serviceIsBound;

    */

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

    public Playlist getPlaylist(String playlistName) {
        return repository.getPlaylist(playlistName);
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

    public void updatePlaylist(Playlist playlist) {
        repository.updatePlaylist(playlist);
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

        for (int i = 0; i < allSongs.size(); i++) {
            if (allSongs.get(i).getSongID() == song.getSongID()) {
                allSongs.remove(i);
                break;
            }
        }
        ArrayList<Song> songs = getQueue().getValue();
        if (songs != null) {
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getSongID() == song.getSongID()) {
                    songs.remove(i);
                    setQueue(songs);
                    break;
                }
            }
        }

        ArrayList<Playlist> playlists = repository.getPlaylistsOfSong(song);
        for (int i = 0; i < playlists.size(); i++) {
            playlists.get(i).songCount--;
            updatePlaylist(playlists.get(i));
        }
        if (playlists.size() > 0) {
            allPlaylists.clear();
            allPlaylists.addAll(repository.getAllPlaylists());
        }

        repository.deleteSong(song);


        Album album;
        for (int i = 0; i < allAlbums.size(); i++) {
            album = allAlbums.get(i);
            if (album.getAlbumName().equals(song.getSongAlbum())) {
                album.songCount--;
                if (album.songCount == 0)
                    allAlbums.remove(i);
                else if (album.imagePath.equals(song.imagePath)) {
                    // TODO - replace image
                    Song temp_song = getSongsOfAlbum(album).get(0);
                    album.imagePath = temp_song.imagePath;
                    try {
                        album.image = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), Uri.parse(temp_song.imagePath));
                    }
                    catch (Exception e) {
                        album.image = null;
                    }
                    repository.updateAlbum(album);
                }
                break;
            }
        }

        Artist artist;
        for (int i = 0; i < allArtists.size(); i++) {
            artist = allArtists.get(i);
            if (artist.getArtistName().equals(song.getSongArtist())) {
                artist.songCount--;
                if (artist.songCount == 0)
                    allArtists.remove(i);
                else if (artist.imagePath.equals(song.imagePath)) {
                    // TODO - replace image
                    Song temp_song = getSongsOfArtist(artist).get(0);
                    artist.imagePath = temp_song.imagePath;
                    try {
                        artist.image = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), Uri.parse(temp_song.imagePath));
                    }
                    catch (Exception e) {
                        artist.image = null;
                    }
                    repository.updateArtist(artist);
                }
                break;
            }
        }
        Toast.makeText(getApplication(), "Song deleted", Toast.LENGTH_SHORT).show();

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

    private final MutableLiveData<ArrayList<Song>> queue = new MutableLiveData<>();
    public void setQueue(ArrayList<Song> queue) {
        this.queue.setValue(queue);
    }
    public LiveData<ArrayList<Song>> getQueue() {
        return queue;
    }

    private final MutableLiveData<Integer> songPosition = new MutableLiveData<>();
    public void setSongPosition(int songPosition) {
        this.songPosition.setValue(songPosition);
    }
    public LiveData<Integer> getSongPosition() {
        return songPosition;
    }

    public Album album = null;
    public void setAlbum(Album album) {
        this.album = album;
    }
    public Album getAlbum() {
        return album;
    }

    public Artist artist = null;
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public Artist getArtist() {
        return artist;
    }

    public Playlist playlist = null;
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
    public Playlist getPlaylist() {
        return playlist;
    }

    public void addToQueue(ArrayList<Song> songs) {
        ArrayList<Song> new_songs = getQueue().getValue();
        if (new_songs == null) {
            Toast.makeText(getApplication(), "Cannot add to empty queue", Toast.LENGTH_SHORT).show();
            return;
        }
        if (songs != null && songs.size() != 0) {
            new_songs.addAll(songs);
            setQueue(new_songs);
            Toast.makeText(getApplication(), "Added to queue", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplication(), "List is empty", Toast.LENGTH_SHORT).show();
    }
    public void addToQueue(Song song) {
        ArrayList<Song> new_songs = getQueue().getValue();
        if (new_songs == null) {
            Toast.makeText(getApplication(), "Cannot add to empty queue", Toast.LENGTH_SHORT).show();
            return;
        }
        if (song != null) {
            new_songs.add(song);
            setQueue(new_songs);
            Toast.makeText(getApplication(), "Added to queue", Toast.LENGTH_SHORT).show();
        }
    }

    public void playQueue(ArrayList<Song> songs, int position) {
        if (songs == null || songs.size() == 0)
            Toast.makeText(getApplication(), "List is empty", Toast.LENGTH_SHORT).show();
        else {
            setQueue(songs);
            setSongPosition(position);
        }
    }
}



