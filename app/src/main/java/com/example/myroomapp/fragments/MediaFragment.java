package com.example.myroomapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.Constants;
import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.example.myroomapp.entities.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
    FloatingActionButton floatingActionButton;
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
            ArrayList<Album> new_albums = new ArrayList<>();
            ArrayList<Artist> new_artists = new ArrayList<>();

            Song song;
            Album album;
            Artist artist;

            for (int i = 0; i < songs_count; i++) {
                if (checked.get(i)) {
                    song = songs.get(i);

                    album = new Album(song.getSongAlbum());
                    album.imagePath = song.imagePath;

                    artist = new Artist(song.getSongArtist());
                    artist.imagePath = song.imagePath;

                    new_songs.add(song);
                    new_albums.add(album);
                    new_artists.add(artist);
                }
            }
            itemViewModel.insertSongs(new_songs);
            itemViewModel.insertAlbums(new_albums);
            itemViewModel.insertArtists(new_artists);
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
        //Toast.makeText(context, "" + num_checked + "/" + songs_count, Toast.LENGTH_SHORT).show();
    }

    private void setSelectAllBtn() {
        if (num_checked < songs_count || songs_count == 0)
            btn_select_all.setText("SELECT ALL");
        else if (songs_count > 0)
            btn_select_all.setText("UNSELECT ALL");
    }
}
