package devchallenge.android.radiotplayer.ui.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.squareup.otto.Subscribe;

import java.util.EventListener;
import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.repository.PodcastsInfoProvider;
import devchallenge.android.radiotplayer.service.PlayerService;

public class MainActivity extends AppCompatActivity implements EventListener {

    //TODO: DELETE AFTER TESTING:
    Button download;
    Button stop;

    private PlayerService mPlayer;
    boolean mIsPlayerBound = false;
    private ServiceConnection mPlayerConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder)service;
            mPlayer = binder.getPlayer();
            mIsPlayerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsPlayerBound = false;
        }
    };

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

        download = (Button) findViewById(R.id.download);
        stop = (Button) findViewById(R.id.stop);

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
    protected void onStart() {
        super.onStart();
        EventManager.getInstance().registerEventListener(this);
        PlayerService.bind(this, mPlayerConnection);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventManager.getInstance().unregisterEventListener(this);
        unbindService(mPlayerConnection);
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
