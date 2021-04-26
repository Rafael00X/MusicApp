package com.example.myroomapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myroomapp.entities.Song;

import java.util.ArrayList;

public class PlayerService extends LifecycleService {
    private static MediaPlayer mediaPlayer = null;
    private static MediaSession mediaSession;
    public static boolean firstSongSelected = false;
    public static ArrayList<Song> queue = new ArrayList<>();
    public static int songPosition = 0;
    public static Song currentSong = null;

    private NotificationManager manager;
    private final IBinder playerBinder = new PlayerBinder();
    private final String CHANNEL_ID = "channel_1";
    private final int NOTIFICATION_ID = 1;

    private static final String PREVIOUS = "Previous";
    private static final String NEXT = "Next";
    private static final String PLAY = "Play";
    private static final String PAUSE = "Pause";



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
        getPlayerTask().observe(this, s -> {

            if (isFirstSongSelected()) {
                switch (s) {
                    case Constants.PLAYER_START:
                        Log.d("From Service", "PLAYER_START");
                        start();
                        startForeground(NOTIFICATION_ID, getNotification(s));
                        //startForeground(NOTIFICATION_ID, getNotification(Constants.PLAYER_PLAY));
                        //setPlayerTask(Constants.PLAYER_PLAY);
                        break;

                    case Constants.PLAYER_PLAY_PAUSE:
                        Log.d("From Service", "PLAYER_PLAY_PAUSE");
                        if (isPlaying()) setPlayerTask(Constants.PLAYER_PAUSE);
                        else setPlayerTask(Constants.PLAYER_PLAY);
                        break;

                    case Constants.PLAYER_PLAY:
                        Log.d("From Service", "PLAYER_PLAY");
                        resume();
                        startForeground(NOTIFICATION_ID, getNotification(s));
                        break;

                    case Constants.PLAYER_PAUSE:
                        Log.d("From Service", "PLAYER_PAUSE");
                        pause();
                        startForeground(NOTIFICATION_ID, getNotification(s));
                        break;

                    case Constants.PLAYER_NEXT:
                        Log.d("From Service", "PLAYER_NEXT");
                        next();
                        setPlayerTask(Constants.PLAYER_PREPARE);
                        break;

                    case Constants.PLAYER_PREVIOUS:
                        Log.d("From Service", "PLAYER_PREVIOUS");
                        previous();
                        setPlayerTask(Constants.PLAYER_PREPARE);
                        break;
                }
            }

            if (s.equals(Constants.PLAYER_PREPARE)) {
                Log.d("From Service", "PLAYER_PREPARE");
                prepare();

            }
        });
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
        stopSelf();
    }

    public class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Music Player Channel", NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription("This is notification"); // Remove
        manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }

    public Notification getNotification(String state) {
        PendingIntent previous_click = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationReceiver.class).setAction(PREVIOUS), 0);
        PendingIntent next_click = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationReceiver.class).setAction(NEXT), 0);
        PendingIntent play_click = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationReceiver.class).setAction(PLAY), 0);
        PendingIntent pause_click = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationReceiver.class).setAction(PAUSE), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_menu_dot)
                .setLargeIcon(currentSong.image)
                .setContentTitle(currentSong.getSongName())
                .setContentText(currentSong.getSongAlbum() + " - " + currentSong.getSongArtist())
                .addAction(R.drawable.ic_previous, "Previous", previous_click)
                .addAction(R.drawable.ic_play, "Play", play_click)
                .addAction(R.drawable.ic_pause, "Pause", pause_click)
                .addAction(R.drawable.ic_next, "Next", next_click);

        if (state.equals(Constants.PLAYER_PAUSE))
            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 3));
        else
            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 2, 3));

        return builder.build();
    }



    public void create() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> setPlayerTask(Constants.PLAYER_NEXT));
        firstSongSelected = false;
        if (Build.VERSION.SDK_INT >= 26)
            createNotificationChannel();
    }

    public void prepare() {
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
        }
    }

    public void start() {
        resume();
    }

    public void destroy() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public static boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isFirstSongSelected() {
        return firstSongSelected;
    }

    public void resume() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    private void next() {
        songPosition = (songPosition + 1 >= queue.size()) ? 0 : songPosition + 1;
    }

    private void previous() {
        songPosition = (songPosition - 1 < 0) ? queue.size() - 1 : songPosition - 1;
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

    public static void addToQueue(ArrayList<Song> songs) {
        queue.addAll(songs);
    }

    private static final MutableLiveData<String> playerTask = new MutableLiveData<>();
    public static void setPlayerTask(String newPlayerTask) {
        playerTask.setValue(newPlayerTask);
    }
    public static LiveData<String> getPlayerTask() {
        return playerTask;
    }


}
