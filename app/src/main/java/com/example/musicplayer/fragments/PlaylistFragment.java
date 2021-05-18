package com.example.musicplayer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Constants;
import com.example.musicplayer.ItemViewModel;
import com.example.musicplayer.R;
import com.example.musicplayer.entities.Playlist;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;


import java.util.ArrayList;

public class PlaylistFragment extends Fragment implements PlaylistAdapter.OnItemClickListener {
    private Dialog dialog;
    private ExtendedFloatingActionButton floating_button;
    private ItemViewModel itemViewModel;
    private PlaylistAdapter adapter;
    private final ArrayList<Playlist> playlists;
    private final Context context;

    public PlaylistFragment(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        dialog = new Dialog(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.playlist_recyclerView);
        adapter = new PlaylistAdapter(playlists, this, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        floating_button = view.findViewById(R.id.floating_button);
        floating_button.setOnClickListener(v -> showAddPlaylist());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
    }

    @Override
    public void onItemClick(Playlist playlist) {
        // TODO - finish
        itemViewModel.setPlaylist(playlist);
        itemViewModel.setLoadFragment(Constants.FRAGMENT_SONG_FROM_PLAYLIST);
    }

    private void showAddPlaylist() {
        dialog.setContentView(R.layout.extra_new_playlist);
        EditText editText = dialog.findViewById(R.id.playlist_name);
        Button btnAdd = dialog.findViewById(R.id.playlist_add);
        Button btnCancel = dialog.findViewById(R.id.playlist_cancel);

        btnAdd.setOnClickListener(v -> {
            String name = editText.getText().toString();
            if (itemViewModel.getPlaylist(name) == null) {
                Playlist playlist = new Playlist(name);
                playlist.songCount = 0;
                itemViewModel.insertPlaylist(playlist);
                playlists.add(itemViewModel.getPlaylist(name));
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            else {
                Toast.makeText(context, "Playlist already exists", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
