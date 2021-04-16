package com.example.myroomapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.myroomapp.Constants;
import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.MusicDatabase;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Song;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class NavigationDrawerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    ItemViewModel itemViewModel;
    private View view;
    private Context context;
    private boolean first_created;
    private int selected_menu_item_id;
    private int selected_menu_item_position;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;


    public NavigationDrawerFragment(Context context) {
        this.context = context;
        this.first_created = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        view = inflater.inflate(R.layout.navigation_drawer_fragment_main, container, false);

        /*

        Toolbar toolbar;
        DrawerLayout drawerLayout;
        NavigationView navigationView;
        ActionBarDrawerToggle actionBarDrawerToggle;

         */
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        toolbar = view.findViewById(R.id.drawer_toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        navigationView = view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = view.findViewById(R.id.navigation_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                itemViewModel.setOtherEvents(Constants.NAVIGATION_DRAWER_OPEN);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                itemViewModel.setOtherEvents(Constants.NAVIGATION_DRAWER_CLOSE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        if (first_created) {
            first_created = false;
            selected_menu_item_id = R.id.songs;
            selected_menu_item_position = 0;
            itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
            navigationView.getMenu().getItem(0).setChecked(true);
            SongFragment songFragment = new SongFragment(context, itemViewModel.getAllSongs()); // Changed
            //SongFragment songFragment = new SongFragment(context, itemViewModel.allSongs);
            fragmentManager = getChildFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.replace(R.id.drawer_fragment_containerView, songFragment);
            fragmentTransaction.commit();
        }
        toolbar.setTitle(navigationView.getMenu().getItem(selected_menu_item_position).getTitle());
        /*

        LinearLayout stickyplayer_clickArea = view.findViewById(R.id.stickyplayer_clickArea);
        ImageView stickyplayer_playpause = view.findViewById(R.id.stickyplayer_playpause);
        TextView textView1 = view.findViewById(R.id.stickyplayer_textView1);
        TextView textView2 = view.findViewById(R.id.stickyplayer_textView2);

        stickyplayer_clickArea.setOnClickListener(v -> itemViewModel.setLoadFragment(Constants.FRAGMENT_PLAYER));
        stickyplayer_playpause.setOnClickListener(v -> itemViewModel.setPlayerTask(Constants.PLAYER_PLAY_PAUSE));

        // Added
        Song song_test = itemViewModel.getCurrentSong();
        if (song_test != null) {
            textView1.setText(song_test.getSongName());
            textView2.setText(song_test.getSongArtist() + " - " + song_test.getSongAlbum());
            String task = itemViewModel.getPlayerTask().toString();
            if (task.equals(Constants.PLAYER_START) || task.equals(Constants.PLAYER_PLAY))
                stickyplayer_playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_round));
        }

        itemViewModel.getPlayerTask().observe((LifecycleOwner) context, s -> {
            switch (s) {
                case Constants.PLAYER_START:
                    Song song = itemViewModel.getCurrentSong();
                    textView1.setText(song.getSongName());
                    textView2.setText(song.getSongArtist() + " - " + song.getSongAlbum());
                    stickyplayer_playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause));
                    break;
                case Constants.PLAYER_PLAY:
                    stickyplayer_playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause));
                    break;

                case Constants.PLAYER_PAUSE:
                    stickyplayer_playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play));
                    break;
            }
        });

         */
        startPostponedEnterTransition();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Don't know if needed
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.navigation_drawer_menu, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view = null;
        context = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();

        if (item_id != selected_menu_item_id) {
            selected_menu_item_id = item_id;
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setReorderingAllowed(true);
                if (item_id == R.id.songs) {
                    selected_menu_item_position = 0;
                    SongFragment songFragment = new SongFragment(context, itemViewModel.getAllSongs());
                    fragmentTransaction.replace(R.id.drawer_fragment_containerView, songFragment);
                }
                else if (item_id == R.id.albums) {
                    selected_menu_item_position = 1;
                    AlbumFragment albumFragment = new AlbumFragment(context, itemViewModel.getAllAlbums());
                    fragmentTransaction.replace(R.id.drawer_fragment_containerView, albumFragment);
                }
                else if (item_id == R.id.artists) {
                    selected_menu_item_position = 2;
                    ArtistFragment artistFragment = new ArtistFragment(context, itemViewModel.getAllArtists());
                    fragmentTransaction.replace(R.id.drawer_fragment_containerView, artistFragment);
                }
                else if (item_id == R.id.playlists) {
                    selected_menu_item_position = 3;
                    PlaylistFragment playlistFragment = new PlaylistFragment(context, itemViewModel.getAllPlaylists());
                    fragmentTransaction.replace(R.id.drawer_fragment_containerView, playlistFragment);
                }
                else if (item_id == R.id.manage_media) {
                    selected_menu_item_position = 4;
                    MediaFragment mediaFragment = new MediaFragment(context);
                    fragmentTransaction.replace(R.id.drawer_fragment_containerView, mediaFragment);
                }
                toolbar.setTitle(navigationView.getMenu().getItem(selected_menu_item_position).getTitle());
                fragmentTransaction.commit();
        }
        DrawerLayout drawerLayout = view.findViewById(R.id.navigation_drawer);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
