package devchallenge.android.radiotplayer.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.squareup.otto.Subscribe;

import java.util.EventListener;

import devchallenge.android.radiotplayer.R;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.sync.SyncManager;

public class MainActivity extends AppCompatActivity implements EventListener {

    private ImageButton mSync;
    private View.OnClickListener mStartSyncListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SyncManager.getInstance().syncPodcastsManualy();

        }
    };
    private View.OnClickListener mCancelSyncListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SyncManager.getInstance().cancelCurrentPodcastsDownload();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                    mSync.clearAnimation();
                    mSync.setImageResource(R.drawable.ic_done_white_24dp);
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
