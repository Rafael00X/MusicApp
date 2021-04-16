package com.example.myroomapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Playlist;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    ItemViewModel itemViewModel;
    ArrayList<Playlist> playlists;
    OnItemClickListener listener;
    Context context;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists, OnItemClickListener listener) {
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
        holder.imageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.imageView.getContext(), holder.imageView);
            popupMenu.inflate(R.menu.playlist_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.playlist_delete:
                        new AlertDialog.Builder(context)
                                .setTitle("Delete Song")
                                .setMessage("Are you sure you want to delete?")
                                .setCancelable(true)
                                .setPositiveButton("YES", (dialog, which) -> {
                                    itemViewModel.deletePlaylist(playlist);
                                    playlists.remove(position);
                                    notifyDataSetChanged();
                                })
                                .setNegativeButton("NO", (dialog, which) -> dialog.cancel())
                                .show();

                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView1;
        ImageView imageView;
        ShapeableImageView shapeableImageView;

        public ViewHolder(@NonNull View view) {
            super(view);
            textView1 = view.findViewById(R.id.gridView_textView1);
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
}