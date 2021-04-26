package com.example.myroomapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.Constants;
import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Song;

import java.util.ArrayList;

public class SongFragment extends Fragment implements SongAdapter.OnItemClickListener {
    private ItemViewModel itemViewModel;
    private final ArrayList<Song> songs;
    private final Context context;

    public SongFragment(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.song_recyclerView);
        SongAdapter adapter = new SongAdapter(songs, this, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
    }

    @Override
    public void onItemClick(ArrayList<Song> songs, int position) {
        itemViewModel.setSongPosition(position);
        itemViewModel.setQueue(songs);
        //itemViewModel.setPlayerTask(Constants.PLAYER_PREPARE);
    }
}


