package devchallenge.android.radiotplayer.repository;

import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.concurrent.Executors;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsLoadedEvent;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.sync.SyncManager;

public class PodcastsInfoProvider {
    private static volatile PodcastsInfoProvider sInstance;

    public static PodcastsInfoProvider getInstance() {
        synchronized (PodcastsInfoProvider.class) {
            if (sInstance == null) {
                sInstance = new PodcastsInfoProvider();
            }
        }
        return sInstance;
    }

    private EventManager mEventManager;
    private SyncManager mSyncManager;

    private PodcastsInfoProvider() {
        mSyncManager = SyncManager.getInstance();
        mEventManager = EventManager.getInstance();
        mEventManager.registerEventListener(this);
    }

    @Subscribe
    public void onPodcastsLoaded(PodcastsLoadedEvent event) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //TODO: DELETE AFTER TESTING:
                Log.d("---Test---", "Received loaded data in provider...");
                mEventManager.postEvent(new PodcastsSyncEvent(PodcastsSyncEvent.Status.FINISHED));

            }
        });
    }

    public void startManualSync() {
        mSyncManager.syncPodcastsManualy();
    }

    public void cancelSync() {
        mSyncManager.cancelCurrentPodcastsSync();
    }
}
