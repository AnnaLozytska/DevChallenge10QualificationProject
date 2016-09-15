package devchallenge.android.radiotplayer.net.sync;

import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.EventListener;

import devchallenge.android.radiotplayer.App;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;
import devchallenge.android.radiotplayer.net.sync.task.PodcastsDownloadTaskAsync;

public class SyncManager implements EventListener {
    private static final String TAG = SyncManager.class.getSimpleName();
    private static final String PODCASTS_DOWNLOAD_TAG = "podcasts_download";

    private static volatile SyncManager sInstance;

    public static SyncManager getInstance() {
        synchronized (SyncManager.class) {
            if (sInstance == null) {
                sInstance = new SyncManager();
            }
        }
        return sInstance;
    }

    private final FirebaseJobDispatcher mDispatcher;
    private PodcastsDownloadTaskAsync mCurrentSyncTask;
    private EventManager mEventManager;

    private SyncManager() {
        Driver driver = new GooglePlayDriver(App.getInstance().getApplicationContext());
        mDispatcher = new FirebaseJobDispatcher(driver);
        mEventManager = EventManager.getInstance();
        mEventManager.registerEventListener(this);
    }

    public void schedulePodcastsSync(int jobStartOffset) {
        Log.d(TAG, "Scheduling podcasts sync with interval " + jobStartOffset);
        Job job = mDispatcher.newJobBuilder()
                .setService(PodcastsSyncService.class)
                .setTag(PODCASTS_DOWNLOAD_TAG)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.executionWindow(jobStartOffset, jobStartOffset + 10))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .build();

        int result = mDispatcher.schedule(job);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            PodcastsSyncEvent syncEvent = new PodcastsSyncEvent(PodcastsSyncEvent.Status.FAILED);
            syncEvent.setError("FirebaseJobDispatcher error code " + result);
            mEventManager.postEvent(syncEvent);
        }
    }

    public void syncPodcastsManualy() {
        Log.d(TAG, "Starting poscasts sync...");
        cancelCurrentPodcastsSync();
        mCurrentSyncTask = new PodcastsDownloadTaskAsync();
        mCurrentSyncTask.execute();
    }

    //FIXME: get better solution
    public void cancelCurrentPodcastsSync() {
        if (mCurrentSyncTask != null) {
            mCurrentSyncTask.cancel();
        }
    }
}
