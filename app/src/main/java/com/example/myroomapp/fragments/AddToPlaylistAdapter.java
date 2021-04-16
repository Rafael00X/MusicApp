package com.example.myroomapp.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.R;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.PlaylistSongCrossRef;
import com.example.myroomapp.entities.Song;
import com.example.myroomapp.entities.relations.SongWithPlaylists;

import java.util.ArrayList;

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder> {
    ArrayList<Playlist> playlists;
    ArrayList<Boolean> checked;
    MediaAdapter.OnItemClickListener listener;

    public AddToPlaylistAdapter(ArrayList<Playlist> playlists, ArrayList<Boolean> checked, MediaAdapter.OnItemClickListener listener) {
        this.playlists = playlists;
        this.checked = checked;
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddToPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.extra_new_songs_adapter_layout, parent, false);
        return new AddToPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToPlaylistAdapter.ViewHolder holder, int position) {
        holder.textView.setText(playlists.get(position).getPlaylistName());
        holder.checkBox.setChecked(checked.get(position));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        public ViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            textView = view.findViewById(R.id.textView);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}


/*

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder> {
    ArrayList<Playlist> playlists;

    public AddToPlaylistAdapter(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddToPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.extra_new_songs_adapter_layout, parent, false);
        return new AddToPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToPlaylistAdapter.ViewHolder holder, int position) {
        holder.textView.setText(playlists.get(position).getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        public ViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            textView = view.findViewById(R.id.textView);
        }
    }
}


 */