package com.example.myroomapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song {
    @PrimaryKey
    private long songID;
    private String songName;
    private String songAlbum;
    private String songArtist;
    private String songLastModified;
    private final String songPath;
    //private final String songDuration;


    public Song(long songID, String songName, String songAlbum, String songArtist, String songLastModified, @NonNull String songPath) {
        this.songID = songID;
        this.songName = songName;
        this.songAlbum = songAlbum;
        this.songArtist = songArtist;
        this.songLastModified = songLastModified;
        this.songPath = songPath;
        //this.songDuration = songDuration;
    }

    public long getSongID() {
        return songID;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongPath() {
        return songPath;
    }

    public String getSongLastModified() {
        return songLastModified;
    }

    public void setSongLastModified(String songLastModified) {
        this.songLastModified = songLastModified;
    }
    /*

    public String getSongDuration() {
        return songDuration;
    }

     */
}
