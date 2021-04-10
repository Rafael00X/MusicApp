package com.example.myroomapp;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.fragments.NavigationDrawerFragment;
import com.example.myroomapp.fragments.PlayerFragment;
import com.example.myroomapp.fragments.Song2Fragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MainActivity extends AppCompatActivity {
    ItemViewModel itemViewModel;
    Functions myFunctions;
    Player player;
    MusicDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFunctions = new Functions();
        player = new Player();
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Permission received", Toast.LENGTH_SHORT).show();
                        player.create(MainActivity.this);
                        //itemViewModel.loadData();

                        /*

                        db = MusicDatabase.getInstance(MainActivity.this);
                        ArrayList<Song> songs = myFunctions.fetchSongs(MainActivity.this);
                        if (songs != null) {
                            for (Song song : songs) {
                                db.songDao().insert(song);
                                db.albumDao().insert(new Album(song.getSongAlbum()));
                                db.artistDao().insert(new Artist(song.getSongArtist()));
                            }
                        }

                         */




                        NavigationDrawerFragment navigationDrawerFragment = new NavigationDrawerFragment(MainActivity.this);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.main_fragment_container_view, navigationDrawerFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();


                        itemViewModel.getLoadFragment().observe(MainActivity.this, s -> {
                            Song2Fragment song2Fragment;
                            PlayerFragment playerFragment;
                            FragmentTransaction transaction;
                            Drawable drawable;
                            switch (s) {
                                case Constants.FRAGMENT_PLAYER:
                                    playerFragment = new PlayerFragment(MainActivity.this, player);
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.setCustomAnimations(
                                            R.anim.bottom_to_top_slide_enter,
                                            R.anim.fade_out,
                                            R.anim.fade_in,
                                            R.anim.top_to_bottom_exit);
                                    transaction.replace(R.id.main_fragment_container_view, playerFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    break;

                                case Constants.FRAGMENT_SONG_FROM_ALBUM:
                                    Album album = itemViewModel.getAlbum();
                                    drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.album_cover);
                                    song2Fragment = new Song2Fragment(MainActivity.this, itemViewModel.getSongsOfAlbum(album), drawable, album.getAlbumName());
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.setCustomAnimations(
                                            R.anim.right_to_left_slide_enter,
                                            R.anim.right_to_left_slide_exit,
                                            R.anim.left_to_right_slide_enter,
                                            R.anim.left_to_right_slide_exit);

                                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    break;

                                case Constants.FRAGMENT_SONG_FROM_ARTIST:
                                    Artist artist = itemViewModel.getArtist();
                                    drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.artist_cover);
                                    song2Fragment = new Song2Fragment(MainActivity.this, itemViewModel.getSongsOfArtist(artist), drawable, artist.getArtistName());
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.setCustomAnimations(
                                            R.anim.right_to_left_slide_enter,
                                            R.anim.right_to_left_slide_exit,
                                            R.anim.left_to_right_slide_enter,
                                            R.anim.left_to_right_slide_exit);
                                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    break;

                                case Constants.FRAGMENT_SONG_FROM_PLAYLIST:
                                    Playlist playlist = itemViewModel.getPlaylist();
                                    drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.playlist_cover);
                                    song2Fragment = new Song2Fragment(MainActivity.this, itemViewModel.getSongsOfPlaylist(playlist), drawable, playlist.getPlaylistName());
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.setCustomAnimations(
                                            R.anim.right_to_left_slide_enter,
                                            R.anim.right_to_left_slide_exit,
                                            R.anim.left_to_right_slide_enter,
                                            R.anim.left_to_right_slide_exit);
                                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    break;

                            }
                        });

                        itemViewModel.getPlayerTask().observe(MainActivity.this, s -> {
                            if (player.isFirstSongSelected()) {
                                switch (s) {
                                    case Constants.PLAYER_START:
                                        player.start();
                                        break;
                                    case Constants.PLAYER_PLAY_PAUSE:
                                        if (player.isPlaying()) itemViewModel.setPlayerTask(Constants.PLAYER_PAUSE);
                                        else itemViewModel.setPlayerTask(Constants.PLAYER_PLAY);
                                        break;

                                    case Constants.PLAYER_PLAY:
                                        player.resume();
                                        break;

                                    case Constants.PLAYER_PAUSE:
                                        player.pause();
                                        break;

                                    case Constants.PLAYER_NEXT:
                                        player.next();
                                        itemViewModel.setPlayerTask(Constants.PLAYER_PREPARE);
                                        break;

                                    case Constants.PLAYER_PREVIOUS:
                                        player.previous();
                                        itemViewModel.setPlayerTask(Constants.PLAYER_PREPARE);
                                        break;
                                }
                            }

                            if (s.equals(Constants.PLAYER_PREPARE)) {
                                // TODO - prevent redundant reload
                                player.setQueue(itemViewModel.getQueue());
                                player.setSongPosition(itemViewModel.getSongPosition());
                                player.prepare();

                            }
                        });
                    }



                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        // TODO
                        Toast.makeText(MainActivity.this, "Permission denied. Unable to access media files.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    @Override
    protected void onDestroy() {
        player.destroy();
        player = null;
        super.onDestroy();
    }
}