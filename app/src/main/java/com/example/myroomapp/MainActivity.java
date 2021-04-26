package com.example.myroomapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.Song;
import com.example.myroomapp.fragments.NavigationDrawerFragment;
import com.example.myroomapp.fragments.PlayerFragment;
import com.example.myroomapp.fragments.Song2Fragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    PlayerService playerService;
    ItemViewModel itemViewModel;
    Functions myFunctions;
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            playerService = ((PlayerService.PlayerBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            playerService = null;
        }
    };
    private boolean serviceIsBound;

    LinearLayout bottomPlayer_clickArea;
    ImageView bottomPlayer_playpause;
    ImageView bottomPlayer_image;
    TextView textView1;
    TextView textView2;
    BottomSheetBehavior<View> bottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFunctions = new Functions();
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Permission received", Toast.LENGTH_SHORT).show();
                        itemViewModel.loadData();

                        NavigationDrawerFragment navigationDrawerFragment = new NavigationDrawerFragment(MainActivity.this);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.main_fragment_container_view, navigationDrawerFragment);
                        fragmentTransaction.addToBackStack(Constants.FRAGMENT_NAVIGATION_TAG);
                        fragmentTransaction.commit();

                        bottomPlayer_clickArea = findViewById(R.id.bottomPlayer_clickArea);
                        bottomPlayer_playpause = findViewById(R.id.bottomPlayer_playpause);
                        bottomPlayer_image = findViewById(R.id.bottomPlayer_image);
                        textView1 = findViewById(R.id.bottomPlayer_textView1);
                        textView2 = findViewById(R.id.bottomPlayer_textView2);
                        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomPlayer_cardView));

                        bottomPlayer_clickArea.setOnClickListener(v -> itemViewModel.setLoadFragment(Constants.FRAGMENT_PLAYER));
                        bottomPlayer_playpause.setOnClickListener(v -> PlayerService.setPlayerTask(Constants.PLAYER_PLAY_PAUSE));

                        itemViewModel.getLoadFragment().observe(MainActivity.this, s -> {
                            Song2Fragment song2Fragment;
                            PlayerFragment playerFragment;
                            FragmentTransaction transaction;
                            Drawable drawable;
                            switch (s) {
                                case Constants.FRAGMENT_PLAYER:
                                    playerFragment = new PlayerFragment(MainActivity.this, playerService);
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.setCustomAnimations(
                                            R.anim.bottom_to_top_slide_enter,
                                            R.anim.fade_out,
                                            R.anim.fade_in,
                                            R.anim.top_to_bottom_exit);
                                    transaction.setReorderingAllowed(true);
                                    transaction.replace(R.id.main_fragment_container_view, playerFragment);
                                    transaction.addToBackStack(Constants.FRAGMENT_PLAYER_TAG);
                                    transaction.commit();
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                                    transaction.setReorderingAllowed(true);
                                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                                    transaction.addToBackStack(Constants.FRAGMENT_SONG2_TAG);
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
                                    transaction.setReorderingAllowed(true);
                                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                                    transaction.addToBackStack(Constants.FRAGMENT_SONG2_TAG);
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
                                    transaction.setReorderingAllowed(true);
                                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                                    transaction.addToBackStack(Constants.FRAGMENT_SONG2_TAG);
                                    transaction.commit();
                                    break;

                            }
                        });

                        itemViewModel.getQueue().observe(MainActivity.this, s -> {
                            PlayerService.queue = s;
                            PlayerService.songPosition = itemViewModel.getSongPosition();
                            PlayerService.setPlayerTask(Constants.PLAYER_PREPARE);
                        });

                        itemViewModel.getPlayerBinder().observe(MainActivity.this, playerBinder -> {
                            playerService = playerBinder.getService();
                            initializeStickyPlayer(PlayerService.currentSong);
                            if (PlayerService.isPlaying())
                                bottomPlayer_playpause.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_pause));

                            PlayerService.getPlayerTask().observe(MainActivity.this, s -> {
                                if (playerService.isFirstSongSelected()) {
                                    switch (s) {
                                        case Constants.PLAYER_START:
                                            Toast.makeText(MainActivity.this, "Start entered", Toast.LENGTH_SHORT).show();
                                            initializeStickyPlayer(PlayerService.currentSong);
                                            bottomPlayer_playpause.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_pause));
                                            Toast.makeText(MainActivity.this, "Start ended", Toast.LENGTH_SHORT).show();
                                            break;

                                        case Constants.PLAYER_PLAY_PAUSE:
                                            //if (playerService.isPlaying()) playerService.setPlayerTask(Constants.PLAYER_PAUSE);
                                            //else playerService.setPlayerTask(Constants.PLAYER_PLAY);
                                            break;

                                        case Constants.PLAYER_PLAY:
                                            //player.resume();

                                            bottomPlayer_playpause.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_pause));
                                            break;

                                        case Constants.PLAYER_PAUSE:
                                            //player.pause();

                                            bottomPlayer_playpause.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_play));
                                            break;

                                        case Constants.PLAYER_NEXT:
                                            //player.next();
                                            //playerService.setPlayerTask(Constants.PLAYER_PREPARE);
                                            break;

                                        case Constants.PLAYER_PREVIOUS:
                                            //player.previous();
                                            //playerService.setPlayerTask(Constants.PLAYER_PREPARE);
                                            break;
                                    }
                                }

                            });

                        });

                        itemViewModel.getOtherEvents().observe(MainActivity.this, s -> {
                            switch (s) {
                                case Constants.NAVIGATION_DRAWER_OPEN:
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    break;

                                case Constants.FRAGMENT_PLAYER_CLOSE:
                                case Constants.NAVIGATION_DRAWER_CLOSE:
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    break;
                            }
                        });

                        startService(new Intent(MainActivity.this, PlayerService.class));
                        doBindService();
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

    void doBindService() {
        bindService(new Intent(this, PlayerService.class),
                itemViewModel.connection,
                Context.BIND_AUTO_CREATE);
        serviceIsBound = true;
    }

    void doUnbindService() {
        if (serviceIsBound) {
            unbindService(connection);
            serviceIsBound = false;
        }
    }

    private void initializeStickyPlayer(Song song) {
        if (song != null) {
            textView1.setText(song.getSongName());
            textView2.setText(song.getSongArtist() + " - " + song.getSongAlbum());
            if (song.image != null)
                bottomPlayer_image.setImageBitmap(song.image);
            else
                bottomPlayer_image.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.music_image));
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;
        if (index == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("YES", (dialog, which) -> {
                        MainActivity.this.finish();
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.cancel())
                    .show();
        }
        else {
            if (fragmentManager.getBackStackEntryAt(index).getName().equals(Constants.FRAGMENT_PLAYER_TAG)) {
                itemViewModel.setOtherEvents(Constants.FRAGMENT_PLAYER_CLOSE);
            }
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }
}