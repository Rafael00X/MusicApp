package com.example.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.Artist;
import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.Song;
import com.example.musicplayer.fragments.NavigationDrawerFragment;
import com.example.musicplayer.fragments.PlayerFragment;
import com.example.musicplayer.fragments.Song2Fragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class MainActivity extends AppCompatActivity {
    //PlayerService playerService;
    //private boolean serviceIsBound;
    PlayerTaskReceiver receiver;
    ItemViewModel itemViewModel;

    LinearLayout bottomPlayer_clickArea;
    ImageView bottomPlayer_playpause;
    ImageView bottomPlayer_image;
    TextView textView1;
    TextView textView2;
    BottomSheetBehavior<View> bottomSheetBehavior;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    public class PlayerTaskReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            itemViewModel.setPlayerTask(intent.getAction());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        receiver = new PlayerTaskReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.PLAYER_PREPARE);
        intentFilter.addAction(Constants.PLAYER_START);
        intentFilter.addAction(Constants.PLAYER_NEXT);
        intentFilter.addAction(Constants.PLAYER_PREVIOUS);
        intentFilter.addAction(Constants.PLAYER_PLAY);
        intentFilter.addAction(Constants.PLAYER_PAUSE);
        registerReceiver(receiver, intentFilter);

        itemViewModel.loadData();

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
            Drawable drawable;

            transaction = fragmentManager.beginTransaction();
            transaction.setReorderingAllowed(true);

            switch (s) {
                case Constants.FRAGMENT_PLAYER:
                    playerFragment = new PlayerFragment(MainActivity.this);
                    transaction.setCustomAnimations(
                            R.anim.bottom_to_top_slide_enter,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.top_to_bottom_exit);
                    transaction.replace(R.id.main_fragment_container_view, playerFragment);
                    transaction.addToBackStack(Constants.FRAGMENT_PLAYER_TAG);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    break;

                case Constants.FRAGMENT_SONG_FROM_ALBUM:
                    Album album = itemViewModel.getAlbum();
                    drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.album_cover);
                    song2Fragment = new Song2Fragment(MainActivity.this, itemViewModel.getSongsOfAlbum(album), drawable, Constants.DATA_ALBUM, album.getAlbumName());
                    transaction.setCustomAnimations(
                            R.anim.right_to_left_slide_enter,
                            R.anim.right_to_left_slide_exit,
                            R.anim.left_to_right_slide_enter,
                            R.anim.left_to_right_slide_exit);
                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                    transaction.addToBackStack(Constants.FRAGMENT_SONG2_TAG);
                    break;

                case Constants.FRAGMENT_SONG_FROM_ARTIST:
                    Artist artist = itemViewModel.getArtist();
                    drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.artist_cover);
                    song2Fragment = new Song2Fragment(MainActivity.this, itemViewModel.getSongsOfArtist(artist), drawable, Constants.DATA_ARTIST, artist.getArtistName());
                    transaction.setCustomAnimations(
                            R.anim.right_to_left_slide_enter,
                            R.anim.right_to_left_slide_exit,
                            R.anim.left_to_right_slide_enter,
                            R.anim.left_to_right_slide_exit);
                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                    transaction.addToBackStack(Constants.FRAGMENT_SONG2_TAG);
                    break;

                case Constants.FRAGMENT_SONG_FROM_PLAYLIST:
                    Playlist playlist = itemViewModel.getPlaylist();
                    drawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.playlist_cover);
                    song2Fragment = new Song2Fragment(MainActivity.this, itemViewModel.getSongsOfPlaylist(playlist), drawable, Constants.DATA_PLAYLIST, playlist.getPlaylistName());
                    transaction.setCustomAnimations(
                            R.anim.right_to_left_slide_enter,
                            R.anim.right_to_left_slide_exit,
                            R.anim.left_to_right_slide_enter,
                            R.anim.left_to_right_slide_exit);
                    transaction.replace(R.id.main_fragment_container_view, song2Fragment);
                    transaction.addToBackStack(Constants.FRAGMENT_SONG2_TAG);
                    break;
            }
            transaction.commit();
        });

        itemViewModel.getQueue().observe(MainActivity.this, s -> {
            PlayerService.queue = s;
        });

        itemViewModel.getSongPosition().observe(MainActivity.this, s -> {
            PlayerService.songPosition = s;
            PlayerService.setPlayerTask(Constants.PLAYER_PREPARE);
        });

        itemViewModel.getPlayerTask().observe(MainActivity.this, s -> {
            switch (s) {
                case Constants.PLAYER_START:
                    initializeStickyPlayer();
                    itemViewModel.current_song = PlayerService.currentSong;
                    break;

                case Constants.PLAYER_PLAY:
                    bottomPlayer_playpause.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_pause));
                    break;

                case Constants.PLAYER_PAUSE:
                    bottomPlayer_playpause.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_play));
                    break;
            }
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

        NavigationDrawerFragment navigationDrawerFragment = new NavigationDrawerFragment(MainActivity.this);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_fragment_container_view, navigationDrawerFragment);
        transaction.addToBackStack(Constants.FRAGMENT_NAVIGATION_TAG);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeStickyPlayer();
        itemViewModel.current_song = PlayerService.currentSong;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;
        if (index == 0) {
            MainActivity.this.finish();
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
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initializeStickyPlayer() {
        Song song = PlayerService.currentSong;
        if (song != null) {
            textView1.setText(song.getSongName());
            textView2.setText(song.getSongArtist() + " - " + song.getSongAlbum());
            if (song.image != null)
                bottomPlayer_image.setImageBitmap(song.image);
            else
                bottomPlayer_image.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.music_image));
            if (PlayerService.isPlaying())
                bottomPlayer_playpause.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_pause));
        }
    }
}