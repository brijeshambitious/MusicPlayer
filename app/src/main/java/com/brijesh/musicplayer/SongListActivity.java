package com.brijesh.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brijesh.musicplayer.Adapter.SongListAdapter;
import com.brijesh.musicplayer.Data.Song;
import com.brijesh.musicplayer.R;
import com.brijesh.musicplayer.Service.MusicControls;
import com.brijesh.musicplayer.Service.MusicService;
import com.brijesh.musicplayer.Service.PlayerFunctions;
import com.brijesh.musicplayer.Util.widget.IndexableListView;
import com.brijesh.musicplayer.Util.widget.PlayerPanel.PlayerPanelView;
import com.brijesh.musicplayer.Util.widget.PlayerPanel.playerview.MusicPlayerView;

public class SongListActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(SongListActivity.this, "Write External Storage permission allows us to do read music files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        }
    }


    static int currentTrack = 0;
    static int pausedProgress = 0;
    RelativeLayout playerPanel;
    public static SeekBar trackseekbar;
    public static SeekBar volumeseekbar;
    public static TextView panelMusicTitle, insideMusicTitle, insideMusicArtist;
    public static ImageView panelPlayPauseBtn, nextBtn, prevBtn;
    public static PlayerPanelView playerPanelView;
    public static RelativeLayout playPauseLayout;
    public static MusicPlayerView mpv;
    static Bitmap bb;
    IndexableListView listView;
    SongListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        trackseekbar = (SeekBar) findViewById(R.id.seekBar);
        bb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_icon);
        volumeseekbar = (SeekBar) findViewById(R.id.seekBar2);
        playerPanel = (RelativeLayout) findViewById(R.id.playerPanel);






        playerPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        listView = (IndexableListView) findViewById(R.id.list);
        listView.setFastScrollEnabled(true);
        adapter = new SongListAdapter( PlayerFunctions.listOfSongs(getApplicationContext()), SongListActivity.this);
        listView.setAdapter(adapter);
        panelMusicTitle = (TextView) findViewById(R.id.textView);
        insideMusicTitle = (TextView) findViewById(R.id.textViewSong);
        insideMusicArtist = (TextView) findViewById(R.id.textViewSinger);
        panelPlayPauseBtn = (ImageView) findViewById(R.id.imageView3);
        nextBtn = (ImageView) findViewById(R.id.next);
        prevBtn = (ImageView) findViewById(R.id.previous);
        mpv = (MusicPlayerView) findViewById(R.id.mpv);

        panelPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicService.mp.isPlaying()) {
                    panelPlayPauseBtn.setImageResource(R.drawable.ic_play);
                    MusicControls.pauseControl(getApplicationContext());
                } else {
                    panelPlayPauseBtn.setImageResource(R.drawable.ic_pause);
                    MusicControls.playControl(getApplicationContext());
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicControls.nextControl(getApplicationContext());
                currentTrack += 1;
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MusicControls.previousControl(getApplicationContext());
                currentTrack -= 1;
            }
        });
        playerPanelView = (PlayerPanelView) findViewById(R.id.sliding_layout);
        playPauseLayout = (RelativeLayout) findViewById(R.id.playPauseLayout);
        if (MusicService.mp != null) {
            if (!MusicService.mp.isPlaying()) {
                playerPanelView.setPanelHeight(0);
            } else {
                playerPanelView.setPanelHeight(playPauseLayout.getHeight());
            }
        } else {

            playerPanelView.setPanelHeight(0);
        }
        playerPanelView.addPanelSlideListener(new PlayerPanelView.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, PlayerPanelView.PanelState previousState, PlayerPanelView.PanelState newState) {
                if (newState == PlayerPanelView.PanelState.EXPANDED) {
                    playPauseLayout.setVisibility(View.GONE);
                } else {
                    playPauseLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }




    public static void updateUI(Song song, final Context activity, boolean pause) {
        playerPanelView.setPanelHeight(playPauseLayout.getHeight());
        panelMusicTitle.setText(song.getName());
        mpv.setProgress(pausedProgress);
        mpv.setBitmapCover(BitmapFactory.decodeResource(activity.getResources(), R.drawable.default_thumbanil));
        mpv.setMax((int) (MusicService.mp.getDuration() / 1000));
        insideMusicTitle.setText(song.getName());
        insideMusicArtist.setText(song.getArtistName());
        trackseekbar.setMax(MusicService.mp.getDuration() / 1000);
        trackseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MusicService.mp.seekTo(progress * 1000);
                mpv.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        volumeseekbar.setMax(100);
        volumeseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                final int MAX_VOLUME = 100;
                float volume = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                MusicService.mp.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicService.mp.isPlaying()) {
                    pausedProgress = MusicService.mp.getCurrentPosition();
                    MusicControls.pauseControl(activity);
                } else {
                    MusicControls.playControl(activity);

                }
            }
        });
        if (pause) {
            mpv.stop();
            panelPlayPauseBtn.setImageResource(R.drawable.ic_play);
        } else {
            mpv.start();
            mpv.setProgress(pausedProgress);
            panelPlayPauseBtn.setImageResource(R.drawable.ic_pause);
        }
    }

    public static int getCurrentTrack() {
        return currentTrack;
    }

    public static void setCurrentTrack(int currentTrack) {
        SongListActivity.currentTrack = currentTrack;
    }
}
