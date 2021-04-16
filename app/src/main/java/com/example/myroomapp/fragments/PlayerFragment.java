package com.example.myroomapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.myroomapp.Constants;
import com.example.myroomapp.ItemViewModel;
import com.example.myroomapp.MainActivity;
import com.example.myroomapp.Player;
import com.example.myroomapp.R;
import com.example.myroomapp.entities.Song;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerFragment extends Fragment {

    SeekBar seekbar;
    ImageView playpause, next, previous, image, menu;
    TextView textView1, textView2, curr_time, max_time;
    ItemViewModel itemViewModel;
    Player player;
    ExecutorService executor;
    Context context;

    public PlayerFragment(Context context, Player player) {
        this.context = context;
        this.player = player;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
        executor = Executors.newSingleThreadExecutor();

        seekbar = view.findViewById(R.id.player_seekbar);
        playpause = view.findViewById(R.id.player_playpause);
        next = view.findViewById(R.id.player_next);
        previous = view.findViewById(R.id.player_previous);
        image = view.findViewById(R.id.player_image);
        menu = view.findViewById(R.id.player_menu);
        textView1 = view.findViewById(R.id.player_textView1);
        textView2 = view.findViewById(R.id.player_textView2);
        curr_time = view.findViewById(R.id.player_current_time);
        max_time = view.findViewById(R.id.player_maximum_time);

        playpause.setOnClickListener(v -> itemViewModel.setPlayerTask(Constants.PLAYER_PLAY_PAUSE));
        next.setOnClickListener(v -> itemViewModel.setPlayerTask(Constants.PLAYER_NEXT));
        previous.setOnClickListener(v -> itemViewModel.setPlayerTask(Constants.PLAYER_PREVIOUS));

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean is_playing;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player.isFirstSongSelected()) {
                    curr_time.setText(getTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (player.isFirstSongSelected()) {
                    is_playing = player.isPlaying();
                    player.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player.isFirstSongSelected()) {
                    player.setCurrentPosition(seekBar.getProgress());
                    if (is_playing) {
                        player.resume();
                    }
                }
            }
        });

        // Added
        Song song_test = itemViewModel.getCurrentSong();
        if (song_test != null) {
            textView1.setText(song_test.getSongName());
            textView2.setText(song_test.getSongArtist() + " - " + song_test.getSongAlbum());
            seekbar.setMax(player.getDuration());
            max_time.setText(getTime(player.getDuration()));
            if (player.isPlaying())
            playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_round));
        }

        itemViewModel.getPlayerTask().observe((LifecycleOwner) context, s -> {
            switch (s) {
                case Constants.PLAYER_START:
                    Song song = itemViewModel.getCurrentSong();
                    textView1.setText(song.getSongName());
                    textView2.setText(song.getSongArtist() + " - " + song.getSongAlbum());
                    seekbar.setMax(player.getDuration());
                    max_time.setText(getTime(player.getDuration()));
                    playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_round));
                    if (song.image != null)
                        image.setImageBitmap(song.image);
                    else
                        image.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.music_image));
                    break;
                case Constants.PLAYER_PLAY:
                    playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_round));
                    break;

                case Constants.PLAYER_PAUSE:
                    playpause.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_round));
                    break;
            }
        });
        startSeek();
        startPostponedEnterTransition();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
        //Toast.makeText(context, "Destroyed", Toast.LENGTH_LONG).show();
    }


    private String getTime(int time) {
        String ans = "";
        time /= 1000;
        int seconds = time % 60;
        time /= 60;
        int minutes = time % 60;
        time /= 60;

        if (time > 0) ans += time + ":";
        ans += ((minutes < 10)? "0" + minutes : minutes) + ":";
        ans += (seconds < 10)? "0" + seconds : seconds;
        return ans;
    }


    private void startSeek() {
        executor.execute(() -> {
            int current_position;
            while (true) {
                current_position = player.getCurrentPosition();
                seekbar.setProgress(current_position);
                try {
                    Thread.sleep(1000);
                }
                catch (Exception e) {
                }
            }
        });
    }
}
