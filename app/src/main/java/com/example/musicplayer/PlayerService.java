package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.entities.Song;

import java.util.ArrayList;

public class PlayerService extends LifecycleService implements AudioManager.OnAudioFocusChangeListener {
    private static MediaPlayer mediaPlayer = null;
    private static AudioManager audioManager;
    //private static MediaSession mediaSession;
    public static boolean firstSongSelected = false;
    public static ArrayList<Song> queue = new ArrayList<>();
    public static int songPosition = 0;
    public static Song currentSong = null;

    private NotificationManager manager;
    private NotificationClickReceiver receiver;
    private final IBinder playerBinder = new PlayerBinder();

    private static final String CHANNEL_ID = "channel_1";
    private static final int NOTIFICATION_ID = 1;
    private static final String PREVIOUS = "Previous";
    private static final String NEXT = "Next";
    private static final String PLAY = "Play";
    private static final String PAUSE = "Pause";
    private static final String TAG = "PlayerService";

    private static class NotificationClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PREVIOUS:
                    setPlayerTask(Constants.PLAYER_PREVIOUS);
                    break;
                case NEXT:
                    setPlayerTask(Constants.PLAYER_NEXT);
                    break;
                case PLAY:
                    setPlayerTask(Constants.PLAYER_PLAY);
                    break;
                case PAUSE:
                    setPlayerTask(Constants.PLAYER_PAUSE);
                    break;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return playerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        create();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        receiver = new NotificationClickReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PREVIOUS);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PAUSE);
        registerReceiver(receiver, intentFilter);
        getPlayerTask().observe(this, s -> {

            if (isFirstSongSelected()) {
                Intent intent = new Intent(s);
                sendBroadcast(intent);
                switch (s) {
                    case Constants.PLAYER_START:
                        Log.d(TAG, "PLAYER_START");
                        setPlayerTask(Constants.PLAYER_PLAY);
                        break;

                    case Constants.PLAYER_PLAY_PAUSE:
                        Log.d(TAG, "PLAYER_PLAY_PAUSE");
                        if (isPlaying()) setPlayerTask(Constants.PLAYER_PAUSE);
                        else setPlayerTask(Constants.PLAYER_PLAY);
                        break;

                    case Constants.PLAYER_PLAY:
                        Log.d(TAG, "PLAYER_PLAY");
                        int p = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                        if (p == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            resume();
                            startForeground(NOTIFICATION_ID, getNotification(s));
                        }
                        else {
                            Toast.makeText(this, "Unable to gain audio focus", Toast.LENGTH_SHORT).show();
                            setPlayerTask(Constants.PLAYER_PAUSE);
                        }
                        break;

                    case Constants.PLAYER_PAUSE:
                        Log.d(TAG, "PLAYER_PAUSE");
                        pause();
                        startForeground(NOTIFICATION_ID, getNotification(s));
                        break;

                    case Constants.PLAYER_NEXT:
                        Log.d(TAG, "PLAYER_NEXT");
                        next();
                        setPlayerTask(Constants.PLAYER_PREPARE);
                        break;

                    case Constants.PLAYER_PREVIOUS:
                        Log.d(TAG, "PLAYER_PREVIOUS");
                        previous();
                        setPlayerTask(Constants.PLAYER_PREPARE);
                        break;
                }
            }

            if (s.equals(Constants.PLAYER_PREPARE)) {
                Log.d(TAG, "PLAYER_PREPARE");
                prepare();
            }
        });
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "Audio focus gained");
                setPlayerTask(Constants.PLAYER_PLAY);
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "Audio focus ducked");
                mediaPlayer.setVolume(0.5f, 0.5f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "Audio focus lost");
                setPlayerTask(Constants.PLAYER_PAUSE);
                break;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
        if (Build.VERSION.SDK_INT >= 26)
            manager.deleteNotificationChannel(CHANNEL_ID);
        unregisterReceiver(receiver);
        audioManager.abandonAudioFocus(this);
    }

    // Not needed
    public class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription("Notification to control media playback"); // Remove
        manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }

    private Notification getNotification(String state) {
        PendingIntent previous_click = PendingIntent.getBroadcast(this, 0, new Intent(PREVIOUS), 0);
        PendingIntent next_click = PendingIntent.getBroadcast(this, 0, new Intent(NEXT), 0);
        PendingIntent play_click = PendingIntent.getBroadcast(this, 0, new Intent(PLAY), 0);
        PendingIntent pause_click = PendingIntent.getBroadcast(this, 0, new Intent(PAUSE), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_song)
                .setLargeIcon(currentSong.image)
                .setContentTitle(currentSong.getSongName())
                .setContentText(currentSong.getSongAlbum() + " - " + currentSong.getSongArtist());

        builder.addAction(R.drawable.ic_previous, "Previous", previous_click);
        if (state.equals(Constants.PLAYER_PAUSE)) builder.addAction(R.drawable.ic_play, "Play", play_click);
        else builder.addAction(R.drawable.ic_pause, "Pause", pause_click);
        builder.addAction(R.drawable.ic_next, "Next", next_click);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2));
        return builder.build();
    }


    private void create() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> setPlayerTask(Constants.PLAYER_NEXT));
        firstSongSelected = false;
        if (Build.VERSION.SDK_INT >= 26)
            createNotificationChannel();
    }

    private void prepare() {
        mediaPlayer.reset();
        try {
            if (!firstSongSelected) firstSongSelected = true;
            currentSong = queue.get(songPosition);
            mediaPlayer.setDataSource(this, Uri.parse(queue.get(songPosition).getSongPath()));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> setPlayerTask(Constants.PLAYER_START));
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Unable to play song", Toast.LENGTH_SHORT).show();
            setPlayerTask(Constants.PLAYER_NEXT);
        }
    }

    private void destroy() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void next() {
        songPosition = (songPosition + 1 >= queue.size()) ? 0 : songPosition + 1;
    }

    private void previous() {
        songPosition = (songPosition - 1 < 0) ? queue.size() - 1 : songPosition - 1;
    }

    public static boolean isPlaying() {
        if (mediaPlayer == null)
            return false;
        return mediaPlayer.isPlaying();
    }

    public static boolean isFirstSongSelected() {
        return firstSongSelected;
    }

    public static void resume() {
        mediaPlayer.start();
    }

    public static void pause() {
        mediaPlayer.pause();
    }

    public static int getDuration() {
        return mediaPlayer.getDuration();
    }

    public static int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public static void setCurrentPosition(int position) {
        mediaPlayer.seekTo(position);
    }

    private static final MutableLiveData<String> playerTask = new MutableLiveData<>();
    public static void setPlayerTask(String newPlayerTask) {
        playerTask.setValue(newPlayerTask);
    }
    public static LiveData<String> getPlayerTask() {
        return playerTask;
    }

}
