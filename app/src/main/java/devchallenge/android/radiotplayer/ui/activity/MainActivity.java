package devchallenge.android.radiotplayer.ui.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsLoadedEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.model.PodcastItem;
import devchallenge.android.radiotplayer.repository.PodcastsInfoProvider;

public class MainActivity extends AppCompatActivity implements EventListener {

    //TODO: DELETE AFTER TESTING:
    private TextView url;
    private Button stop;
    private MediaPlayer player;

    private ImageButton mSync;
    private View.OnClickListener mStartSyncListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PodcastsInfoProvider.getInstance().startManualSync();

        }
    };
    private View.OnClickListener mCancelSyncListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PodcastsInfoProvider.getInstance().cancelSync();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = (TextView) findViewById(R.id.playing_url);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
            }
        });
        player = new MediaPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: disable sync if no network available
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mSync = (ImageButton) menu.findItem(R.id.manual_sync).getActionView();
        mSync.setOnClickListener(mStartSyncListener);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventManager.getInstance().registerEventListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventManager.getInstance().unregisterEventListener(this);
    }

    //TODO: DELETE AFTER TESTING:
    @Subscribe
    public void onPodcLoaded(PodcastsLoadedEvent event) {
        List<PodcastItem> podcasts = event.getPodcasts();
        String audioUrl = podcasts.get(0).getEnclosureUrl();
        url.setText(audioUrl);
        Uri audioUri = Uri.parse(audioUrl);
        try {
            //TODO warn user if network speed is low for streaming
            player.setDataSource(this, audioUri);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onSyncUpdates(PodcastsSyncEvent event) {
        if (mSync != null) {
            switch (event.getStatus()) {
                case STARTED:
                    mSync.setImageResource(R.drawable.ic_sync_white_24dp);
                    Animation rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.sync_rotation);
                    rotation.setRepeatCount(Animation.INFINITE);
                    mSync.startAnimation(rotation);
                    mSync.setOnClickListener(mCancelSyncListener);
                    break;
                case FINISHED:
                    boolean isRecent =
                            (System.currentTimeMillis() - event.getEventTimestamp())
                                    <= TimeUnit.MINUTES.toMillis(5);
                    mSync.clearAnimation();
                    mSync.setImageResource(isRecent ? R.drawable.ic_done_white_24dp
                            : R.drawable.ic_sync_white_24dp);
                    mSync.setOnClickListener(mStartSyncListener);
                    break;
                case FAILED:
                    mSync.clearAnimation();
                    mSync.setImageResource(R.drawable.ic_error_white_24dp);
                    mSync.setOnClickListener(mStartSyncListener);
                    break;
                default:
                    mSync.clearAnimation();
                    mSync.setImageResource(R.drawable.ic_sync_white_24dp);
                    mSync.setOnClickListener(mStartSyncListener);
            }
        }
    }
}
