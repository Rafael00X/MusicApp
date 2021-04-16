package com.example.myroomapp.fragments;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.Constants;
import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.MusicDatabase;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;

import java.util.ArrayList;

public class ArtistFragment extends Fragment implements ArtistAdapter.OnItemClickListener {
    private ItemViewModel itemViewModel;
    private final ArrayList<Artist> artists;
    private final Context context;

    public ArtistFragment(Context context, ArrayList<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.artist_recyclerView);
        ArtistAdapter adapter = new ArtistAdapter(artists, this, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class); // requireActivity()
    }

    @Override
    public void onItemClick(Artist artist) {
        itemViewModel.setArtist(artist);
        itemViewModel.setLoadFragment(Constants.FRAGMENT_SONG_FROM_ARTIST);
    }
}
