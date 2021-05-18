package com.example.musicplayer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.ItemViewModel;
import com.example.musicplayer.R;
import com.example.musicplayer.entities.Playlist;
import com.example.musicplayer.entities.Song;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    ItemViewModel itemViewModel;
    ArrayList<Playlist> playlists;
    OnItemClickListener listener;
    Context context;

    public PlaylistAdapter(ArrayList<Playlist> playlists, OnItemClickListener listener, Context context) {
        this.playlists = playlists;
        this.listener = listener;
        this.context = context;
        this.itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_layout_gridview, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.textView1.setText(playlist.getPlaylistName());
        holder.textView2.setText(playlist.songCount + " Songs");
        holder.imageView.setOnClickListener(v -> showPopupMenu(holder, position));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView1, textView2;
        ImageView imageView;
        ShapeableImageView shapeableImageView;

        public ViewHolder(@NonNull View view) {
            super(view);
            textView1 = view.findViewById(R.id.gridView_textView1);
            textView2 = view.findViewById(R.id.gridView_textView2);
            imageView = view.findViewById(R.id.gridView_imageView2);
            shapeableImageView = view.findViewById(R.id.gridView_imageView1);
            shapeableImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(playlists.get(position));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Playlist playlist);
    }

    public void showPopupMenu(PlaylistAdapter.ViewHolder holder, int position) {
        PopupMenu popupMenu = new PopupMenu(holder.imageView.getContext(), holder.imageView);
        popupMenu.inflate(R.menu.playlist_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            ArrayList<Song> songs;
            switch (item.getItemId()) {
                case R.id.playlist_play:
                    songs = itemViewModel.getSongsOfPlaylist(playlists.get(position));
                    itemViewModel.playQueue(songs, 0);
                    break;
                case R.id.playlist_addToQueue:
                    songs = itemViewModel.getSongsOfPlaylist(playlists.get(position));
                    itemViewModel.addToQueue(songs);
                    break;
                case R.id.playlist_rename:
                    showRenamePlaylist(position);
                    break;
                case R.id.playlist_delete:
                    showDeleteConfirmation(position);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    public void showDeleteConfirmation(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete?")
                .setCancelable(true)
                .setPositiveButton("YES", (dialog, which) -> {
                    itemViewModel.deletePlaylist(playlists.get(position));
                    playlists.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.cancel())
                .show();
    }

    public void showRenamePlaylist(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.extra_rename_playlist);
        EditText editText = dialog.findViewById(R.id.playlist_name);
        editText.setText(playlists.get(position).getPlaylistName());
        Button btnRename = dialog.findViewById(R.id.playlist_rename);
        Button btnCancel = dialog.findViewById(R.id.playlist_cancel);

        btnRename.setOnClickListener(v -> {
            String name = editText.getText().toString();
            Playlist playlist = playlists.get(position);
            if (itemViewModel.getPlaylist(name) == null) {
                playlist.setPlaylistName(name);
                itemViewModel.updatePlaylist(playlist);
                notifyDataSetChanged();
                dialog.dismiss();
            }
            else if (! playlist.getPlaylistName().equals(name)){
                Toast.makeText(context, "Playlist already exists", Toast.LENGTH_SHORT).show();
            }
            else
                dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}