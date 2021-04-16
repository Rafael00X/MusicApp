package com.example.myroomapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.MainActivity;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Playlist;
import com.example.myroomapp.entities.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    ItemViewModel itemViewModel;
    ArrayList<Song> songs;
    OnItemClickListener listener;
    Context context;


    public SongAdapter(ArrayList<Song> songs, OnItemClickListener listener, Context context) {
        this.songs = songs;
        this.listener = listener;
        this.context = context;
        this.itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
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
        holder.imageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.imageView.getContext(), holder.imageView);
            popupMenu.inflate(R.menu.song_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.song_addToPlaylist:
                        showPopup(position);
                        break;
                    case R.id.song_delete:
                        new AlertDialog.Builder(context)
                                .setTitle("Delete Song")
                                .setMessage("Are you sure you want to delete?")
                                .setCancelable(true)
                                .setPositiveButton("YES", (dialog, which) -> {
                                    itemViewModel.deleteSong(song);
                                    songs.remove(position);
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

    public void showPopup(int position) {

        ArrayList<Playlist> playlists = itemViewModel.getAllPlaylists();
        ArrayList<Playlist> curr_playlists = itemViewModel.getPlaylistsOfSong(songs.get(position));
        ArrayList<Boolean> checked = new ArrayList<>();
        
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

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.extra_add_to_playlists);
        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> {
            dialog.dismiss();
            ArrayList<Playlist> new_playlists = new ArrayList<>();
            for (int i = 0; i < playlists.size(); i++) {
                if (checked.get(i))
                    new_playlists.add(playlists.get(i));
                else
                    itemViewModel.deleteSongFromPlaylist(songs.get(position), playlists.get(i));
            }
            itemViewModel.insertPlaylistsOfSong(songs.get(position), new_playlists);
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.extra_add_to_playlist_recyclerView);
        AddToPlaylistAdapter adapter = new AddToPlaylistAdapter(playlists, checked, position1 -> checked.set(position1, ! checked.get(position1)));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        dialog.show();


        /*

        ArrayList<Playlist> playlists = itemViewModel.getAllPlaylists();
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.extra_add_to_playlists);

        RecyclerView recyclerView = dialog.findViewById(R.id.extra_add_to_playlist_recyclerView);
        AddToPlaylistAdapter adapter = new AddToPlaylistAdapter(playlists);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        dialog.show();

         */
    }
}
