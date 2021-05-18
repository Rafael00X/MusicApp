package com.example.musicplayer.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.ItemViewModel;
import com.example.musicplayer.R;
import com.example.musicplayer.entities.Album;
import com.example.musicplayer.entities.Song;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    ItemViewModel itemViewModel;
    ArrayList<Album> albums;
    OnItemClickListener listener;
    Context context;

    public AlbumAdapter(ArrayList<Album> albums, OnItemClickListener listener, Context context) {
        this.albums = albums;
        this.listener = listener;
        this.context = context;
        this.itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_layout_gridview, parent, false);
        return new AlbumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.textView1.setText(album.getAlbumName());
        holder.textView2.setText(album.songCount + " Songs");
        if (album.image != null)
            holder.shapeableImageView.setImageBitmap(album.image);
        else
            holder.shapeableImageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.album_cover));
        holder.imageView.setOnClickListener(v -> showPopupMenu(holder, position));
    }

    @Override
    public int getItemCount() {
        return albums.size();
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
                listener.onItemClick(albums.get(position));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Album album);
    }

    public void showPopupMenu(AlbumAdapter.ViewHolder holder, int position) {
        PopupMenu popupMenu = new PopupMenu(holder.imageView.getContext(), holder.imageView);
        popupMenu.inflate(R.menu.album_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            ArrayList<Song> songs;
            switch (item.getItemId()) {
                case R.id.album_play:
                    songs = itemViewModel.getSongsOfAlbum(albums.get(position));
                    itemViewModel.playQueue(songs, 0);
                    break;
                case R.id.album_addToQueue:
                    songs = itemViewModel.getSongsOfAlbum(albums.get(position));
                    itemViewModel.addToQueue(songs);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
}