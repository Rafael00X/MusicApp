package com.example.myroomapp.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
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

public class Song2Fragment extends Fragment implements SongAdapter.OnItemClickListener {
    private ItemViewModel itemViewModel;
    private final ArrayList<Song> songs;
    private final String name;
    private final Context context;

    RecyclerView recyclerView;
    SongAdapter adapter;
    TextView textView;
    ImageView imageView;
    ImageButton imageButton;
    Drawable drawable;

    public Song2Fragment(Context context, ArrayList<Song> songs, Drawable drawable, String name) {
        this.context = context;
        this.songs = songs;
        this.drawable = drawable;
        this.name = name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song2, container, false);
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new SongAdapter(songs, this, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        textView = view.findViewById(R.id.textView);
        textView.setText(name);

        imageView = view.findViewById(R.id.imageView);
        imageView.setImageDrawable(drawable);
        imageButton = view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(v -> {
            onDestroy();
            getParentFragmentManager().popBackStackImmediate();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(ArrayList<Song> songs, int position) {
        itemViewModel.setSongPosition(position);
        itemViewModel.setQueue(songs);
        //itemViewModel.setPlayerTask(Constants.PLAYER_PREPARE);
    }
}
