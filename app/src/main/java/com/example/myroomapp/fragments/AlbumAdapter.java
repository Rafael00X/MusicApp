package com.example.myroomapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.MusicDatabase;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Album;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    ArrayList<Album> albums;
    OnItemClickListener listener;
    Context context;

    public AlbumAdapter(ArrayList<Album> albums, OnItemClickListener listener, Context context) {
        this.albums = albums;
        this.listener = listener;
        this.context = context;
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
        if (album.image != null)
            holder.shapeableImageView.setImageBitmap(album.image);
        else
            holder.shapeableImageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.album_cover));
        holder.imageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.imageView.getContext(), holder.imageView);
            popupMenu.inflate(R.menu.album_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.album_play:
                        break;
                    case R.id.album_add_to_queue:
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
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
                listener.onItemClick(albums.get(position));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Album album);
    }
}