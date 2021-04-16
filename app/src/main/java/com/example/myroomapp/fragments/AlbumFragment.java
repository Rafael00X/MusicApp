package com.example.myroomapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.Constants;
import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.MusicDatabase;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Song;
import com.example.myroomapp.entities.relations.AlbumWithSongs;

import java.util.ArrayList;

public class AlbumFragment extends Fragment implements AlbumAdapter.OnItemClickListener {
    private ItemViewModel itemViewModel;
    private final ArrayList<Album> albums;
    private final Context context;

    public AlbumFragment(Context context, ArrayList<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.album_recyclerView);
        AlbumAdapter adapter = new AlbumAdapter(albums, this, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
    }

    @Override
    public void onItemClick(Album album) {
        itemViewModel.setAlbum(album);
        itemViewModel.setLoadFragment(Constants.FRAGMENT_SONG_FROM_ALBUM);
    }
}
