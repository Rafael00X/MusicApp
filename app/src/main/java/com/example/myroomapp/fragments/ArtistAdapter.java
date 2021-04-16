package com.example.myroomapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myroomapp.R;
import com.example.myroomapp.entities.Album;
import com.example.myroomapp.entities.Artist;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    ArrayList<Artist> artists;
    OnItemClickListener listener;
    Context context;

    public ArtistAdapter(ArrayList<Artist> artists, OnItemClickListener listener, Context context) {
        this.artists = artists;
        this.listener = listener;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_layout_gridview, parent, false);
        return new ArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.ViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.textView1.setText(artist.getArtistName());
        if (artist.image != null)
            holder.shapeableImageView.setImageBitmap(artist.image);
        else
            holder.shapeableImageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.artist_cover));
        holder.imageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.imageView.getContext(), holder.imageView);
            popupMenu.inflate(R.menu.artist_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.artist_play:
                        break;
                    case R.id.artist_add_to_queue:
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
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
                listener.onItemClick(artists.get(position));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Artist artist);
    }
}