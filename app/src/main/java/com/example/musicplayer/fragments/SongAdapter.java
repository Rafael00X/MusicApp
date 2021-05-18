package com.example.musicplayer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Constants;
import com.example.musicplayer.ItemViewModel;
import com.example.musicplayer.R;
import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    ItemViewModel itemViewModel;
    ArrayList<Song> songs;
    OnItemClickListener listener;
    Context context;
    String parent;

    public SongAdapter(ArrayList<Song> songs, OnItemClickListener listener, Context context, String parent) {
        this.songs = songs;
        this.listener = listener;
        this.context = context;
        this.itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
        this.parent = parent;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_layout_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.textView1.setText(song.getSongName());
        holder.textView2.setText(song.getSongArtist() + " - " + song.getSongAlbum());
        if (song.image != null)
            holder.imageView1.setImageBitmap(song.image);
        else
            holder.imageView1.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.music_image));
        holder.imageView.setOnClickListener(v -> showPopupMenu(holder, position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView1, imageView;
        TextView textView1, textView2;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View view) {
            super(view);
            imageView1 = view.findViewById(R.id.imageView1);
            imageView = view.findViewById(R.id.listView_imageView);
            textView1 = view.findViewById(R.id.listView_textView1);
            textView2 = view.findViewById(R.id.listView_textView2);
            constraintLayout = view.findViewById(R.id.listView_clickArea);
            constraintLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(songs, position);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ArrayList<Song> queue, int position);
    }

    public void showPopupMenu(SongAdapter.ViewHolder holder, int position) {
        PopupMenu popupMenu = new PopupMenu(holder.imageView.getContext(), holder.imageView);
        popupMenu.inflate(R.menu.song_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.song_play:
                    itemViewModel.playQueue(songs, position);
                    break;
                case R.id.song_addToQueue:
                    itemViewModel.addToQueue(songs.get(position));
                    break;
                case R.id.song_addToPlaylist:
                    showPlaylistList(position);
                    break;
                case R.id.song_delete:
                    if (itemViewModel.current_song != null && itemViewModel.current_song.getSongID() == songs.get(position).getSongID())
                        Toast.makeText(context, "Song currently being played", Toast.LENGTH_SHORT).show();
                    else
                        showDeleteConfirmation(position);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    public void showPlaylistList(int position) {
        ArrayList<Playlist> playlists = itemViewModel.getAllPlaylists();
        ArrayList<Playlist> curr_playlists = itemViewModel.getPlaylistsOfSong(songs.get(position));
        ArrayList<Boolean> checked = new ArrayList<>();
        ArrayList<Boolean> checkedInitial;

        boolean found;
        for (int i = 0; i < playlists.size(); i++) {
            found = false;
            for (int j = 0; j < curr_playlists.size(); j++) {
                if (playlists.get(i).getPlaylistID() == curr_playlists.get(j).getPlaylistID()) {
                    found = true;
                    break;
                }
            }
            checked.add(found);
        }

        checkedInitial = new ArrayList<>(checked);

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.extra_add_to_playlists);
        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> {
            dialog.dismiss();
            ArrayList<Playlist> new_playlists = new ArrayList<>();
            boolean toRemove = false;
            for (int i = 0; i < playlists.size(); i++) {
                if (checked.get(i) && ! checkedInitial.get(i)) {
                    playlists.get(i).songCount++;
                    new_playlists.add(playlists.get(i));
                }
                else if (! checked.get(i) && checkedInitial.get(i)) {
                    playlists.get(i).songCount--;
                    itemViewModel.deleteSongFromPlaylist(songs.get(position), playlists.get(i));
                    if (parent.equals(Constants.DATA_PLAYLIST) && itemViewModel.playlist.getPlaylistName().equals(playlists.get(i).getPlaylistName()))
                        toRemove = true;
                }
            }
            itemViewModel.insertPlaylistsOfSong(songs.get(position), new_playlists);
            if (toRemove) {
                songs.remove(position);
                notifyDataSetChanged();
            }
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.extra_add_to_playlist_recyclerView);
        AddToPlaylistAdapter adapter = new AddToPlaylistAdapter(playlists, checked, position1 -> checked.set(position1, ! checked.get(position1)));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        dialog.show();
    }

    public void showDeleteConfirmation(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete?")
                .setCancelable(true)
                .setPositiveButton("YES", (dialog, which) -> {
                    itemViewModel.deleteSong(songs.get(position));
                    songs.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.cancel())
                .show();
    }
}
