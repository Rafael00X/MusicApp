package com.example.musicplayer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Constants;
import com.example.musicplayer.ItemViewModel;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.PlayerService;
import com.example.musicplayer.R;
import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.Artist;
import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.Song;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MediaFragment extends Fragment implements MediaAdapter.OnItemClickListener {
    private ItemViewModel itemViewModel;
    private int num_checked;
    private int songs_count;
    private boolean is_visible;
    private final ArrayList<Song> songs;
    private final ArrayList<Boolean> checked;
    private final Context context;

    MediaAdapter adapter;
    ExtendedFloatingActionButton floatingActionButton;
    Button btn_select_all;
    Button btn_add;
    Button btn_cancel;

    public MediaFragment(Context context) {
        this.context = context;
        this.songs = new ArrayList<>();
        this.checked = new ArrayList<>();
        this.songs_count = 0;
        this.num_checked = 0;
        this.is_visible = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.media_recyclerView);
        adapter = new MediaAdapter(songs, checked, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        ConstraintLayout constraintLayout = view.findViewById(R.id.add_songs_panel);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        btn_select_all = view.findViewById(R.id.btn_select_all);
        btn_add = view.findViewById(R.id.btn_add);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        if (is_visible)
            constraintLayout.setVisibility(ViewGroup.VISIBLE);

        floatingActionButton.setOnClickListener(v -> {
                    Dexter.withContext(context)
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {

                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    is_visible = true;
                                    constraintLayout.setVisibility(View.VISIBLE);
                                    floatingActionButton.setVisibility(View.GONE);

                                    ArrayList<Song> temp = itemViewModel.getAllSongs();
                                    HashSet<Long> hashSet = new HashSet<>();
                                    for (Song song : temp) {
                                        hashSet.add(song.getSongID());
                                    }
                                    temp = itemViewModel.fetchSongsFromMediaStore(context);
                                    songs.clear();
                                    checked.clear();
                                    for (Song song : temp) {
                                        if (hashSet.contains(song.getSongID())) continue;
                                        songs.add(song);
                                        checked.add(false);
                                    }

                                    adapter.notifyDataSetChanged();
                                    num_checked = 0;
                                    songs_count = songs.size();
                                    setSelectAllBtn();
                                    Toast.makeText(getContext(),"New songs found: " + songs.size(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    // TODO
                                    Toast.makeText(context, "Permission needed to access media files", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
        });

        btn_cancel.setOnClickListener(v -> {
            is_visible = false;
            constraintLayout.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
        });

        btn_add.setOnClickListener(v -> {
            is_visible = false;
            constraintLayout.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);

            ArrayList<Song> new_songs = new ArrayList<>();
            HashMap<String, Album> new_albums = new HashMap<>();
            HashMap<String, Artist> new_artists = new HashMap<>();

            Song song;
            Album album, temp_album;
            Artist artist, temp_artist;

            for (int i = 0; i < songs_count; i++) {
                if (checked.get(i)) {
                    song = songs.get(i);

                    album = new Album(song.getSongAlbum());
                    album.songCount = 1;
                    album.imagePath = song.imagePath;

                    artist = new Artist(song.getSongArtist());
                    artist.songCount = 1;
                    artist.imagePath = song.imagePath;

                    new_songs.add(song);

                    temp_album = new_albums.get(album.getAlbumName());
                    album.songCount = (temp_album == null)? 1 : temp_album.songCount + 1;
                    new_albums.put(album.getAlbumName(), album);

                    temp_artist = new_artists.get(artist.getArtistName());
                    artist.songCount = (temp_artist == null) ? 1 : temp_artist.songCount + 1;
                    new_artists.put(artist.getArtistName(), artist);
                }
            }

            itemViewModel.insertSongs(new_songs);
            itemViewModel.insertAlbums(new ArrayList<>(new_albums.values()));
            itemViewModel.insertArtists(new ArrayList<>(new_artists.values()));
            itemViewModel.loadData();
            Toast.makeText(context,"Songs added: " + new_songs.size(), Toast.LENGTH_SHORT).show();
        });

        btn_select_all.setOnClickListener(v -> {
            if (num_checked < songs_count) {
                num_checked = songs_count;
                for (int i = 0; i < songs_count; i++)
                    checked.set(i, true);
            }
            else if (songs_count > 0) {
                num_checked = 0;
                for (int i = 0; i < songs_count; i++)
                    checked.set(i, false);
            }
            setSelectAllBtn();
            adapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(int position) {
        checked.set(position, ! (checked.get(position)));
        if (checked.get(position)) num_checked++;
        else num_checked--;
        setSelectAllBtn();
    }

    private void setSelectAllBtn() {
        if (num_checked < songs_count || songs_count == 0)
            btn_select_all.setText("SELECT ALL");
        else if (songs_count > 0)
            btn_select_all.setText("UNSELECT ALL");
    }
}
