package com.example.myroomapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.Song;
import com.example.myroomapp.entities.relations.AlbumWithSongs;
import com.example.myroomapp.entities.relations.ArtistWithSongs;
import com.example.myroomapp.entities.relations.PlaylistSongCrossRef;
import com.example.myroomapp.entities.relations.PlaylistWithSongs;
import com.example.myroomapp.entities.relations.SongWithPlaylists;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView textView1, textView2;
    List<SongWithPlaylists> dataList;
    SongWithPlaylists data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MusicDatabase db = MusicDatabase.getInstance(this);

        List<Song> songs = new ArrayList<Song>();
        songs.add(new Song("You Belong With Me", "Fearless", "Taylor Swift", "A"));
        songs.add(new Song("Love Story", "Fearless", "Taylor Swift", "B"));
        songs.add(new Song("Endgame", "Reputation", "Taylor Swift", "C"));
        songs.add(new Song("Dangerous", "NA", "Deamn", "D"));

        List<Album> albums = new ArrayList<Album>();
        albums.add(new Album("Fearless"));
        albums.add(new Album("Reputation"));
        albums.add(new Album("NA"));

        List<Artist> artists = new ArrayList<Artist>();
        artists.add(new Artist("Taylor Swift"));
        artists.add(new Artist("Deamn"));

        List<Playlist> playlists = new ArrayList<Playlist>();
        playlists.add(new Playlist("MyPlaylist1"));
        playlists.add(new Playlist("MyPlaylist2"));

        List<PlaylistSongCrossRef> playlistSongCrossRefs = new ArrayList<PlaylistSongCrossRef>();
        playlistSongCrossRefs.add(new PlaylistSongCrossRef(1, "MyPlaylist1"));
        playlistSongCrossRefs.add(new PlaylistSongCrossRef(2, "MyPlaylist1"));
        playlistSongCrossRefs.add(new PlaylistSongCrossRef(1, "MyPlaylist2"));


        //db.songDao().deleteAll();

        //for (int i = 0; i < songs.size(); i++)
            //db.songDao().insert(songs.get(i));
        for (int i = 0; i < albums.size(); i++)
            db.albumDao().insert(albums.get(i));
        for (int i = 0; i < artists.size(); i++)
            db.artistDao().insert(artists.get(i));
        for (int i = 0; i < playlists.size(); i++)
            db.playlistDao().insert(playlists.get(i));
        for (int i = 0; i < playlists.size(); i++)
            db.playlistDao().insert(playlists.get(i));
        for (int i = 0; i < playlistSongCrossRefs.size(); i++)
            db.playlistSongCrossRefDao().insert(playlistSongCrossRefs.get(i));

        dataList = db.songDao().getSongWithPlaylists(1);
        if (dataList.size() > 0) {
            data = dataList.get(0);
            String TAG = "MyActivity";
            Log.d(TAG, "Number of playlists = " + data.playlists.size());
            for (int i = 0; i < data.playlists.size(); i++) {
                Log.d(TAG, "Playlist name = " + data.playlists.get(i).getPlaylistName());
            }
        }
        Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
    }
}