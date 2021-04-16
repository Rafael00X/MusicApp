package com.example.myroomapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myroomapp.entities.Song;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class Functions {

    // TODO - clean

    protected ArrayList<Song> fetchSongs(Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //Log.d("Uri", "Uri of songs ---------->>>>> " + uri.toString());
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String lastModified = Long.toString(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));
                String path = uri.toString() + File.separator + id;

                songs.add(new Song(id, name, album, artist, lastModified, path));
            }
            cursor.close();
        }

        return songs;
    }

    /*


    protected boolean isValidSong(Song song) {
        File file = new File(song.getSongPath());
        if (! file.exists()) return false;
        if (Long.toString(file.lastModified()).equals(song.getSongLastModified())) return false;
        /*
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getPath());
        if (! song.getSongDuration().equals(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))) return false;
        if (! song.getSongDuration().equals(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))) return false;
        // End comment here
        return true;
    }

    protected ArrayList<Integer> validateCurrentSongs(ArrayList<Song> currentSongs) {
        ArrayList<Integer> invalidSongIDs = new ArrayList<>();
        Song song;
        for (int i = 0; i < currentSongs.size(); i++) {
            song = currentSongs.get(i);
            if (! isValidSong(song)) {
                invalidSongIDs.add(song.getSongID());
            }
        }
        return invalidSongIDs;
    }
    */

}



