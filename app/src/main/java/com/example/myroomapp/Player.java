package com.example.myroomapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.myroomapp.entities.Song;

import java.util.ArrayList;

public class Player {
    ItemViewModel itemViewModel;
    private static MediaPlayer mediaPlayer = null;
    private Context context = null;
    private static boolean firstSongSelected;
    private static ArrayList<Song> queue;
    private static int songPosition;

    public void create(@NonNull Context context) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> itemViewModel.setPlayerTask(Constants.PLAYER_NEXT));
        firstSongSelected = false;
        this.context = context.getApplicationContext();
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
    }

    public void prepare() {
        if (!firstSongSelected) firstSongSelected = true;
        mediaPlayer.reset();
        try {
            itemViewModel.setCurrentSong(queue.get(songPosition));
            mediaPlayer.setDataSource(context, Uri.parse(queue.get(songPosition).getSongPath()));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> itemViewModel.setPlayerTask(Constants.PLAYER_START));
        }
        catch (Exception e) {
            Toast.makeText(context,"Unable to play song", Toast.LENGTH_SHORT).show();
        }
    }

    public void start() {
        mediaPlayer.start();
        itemViewModel.mediaIsPlaying = true;
    }

    public void destroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        context = null;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isFirstSongSelected() {
        return firstSongSelected;
    }

    public void resume() {
        mediaPlayer.start();
        itemViewModel.mediaIsPlaying = true;
    }

    public void pause() {
        mediaPlayer.pause();
        itemViewModel.mediaIsPlaying = false;
    }

    public void next() {
        int next_position = (songPosition + 1 >= queue.size()) ? 0 : songPosition + 1;
        itemViewModel.setSongPosition(next_position);
    }

    public void previous() {
        int previous_position = (songPosition - 1 < 0) ? queue.size() - 1 : songPosition - 1;
        itemViewModel.setSongPosition(previous_position);
    }

    public void setQueue(ArrayList<Song> queue) {
        Player.queue = queue;
    }

    public void setSongPosition(int songPosition) {
        Player.songPosition = songPosition;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void setCurrentPosition(int position) {
        mediaPlayer.seekTo(position);
    }
}