package devchallenge.android.radiotplayer.net.sync;

import android.os.CountDownTimer;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.App;
import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.PodcastsSyncEvent;

public class SyncManager {
    private static final String TAG = SyncManager.class.getSimpleName();
    private static final String TAG_FEED_SYNC = "feed_sync_tag";

    private static volatile SyncManager sInstance;

    public static SyncManager getInstance() {
        synchronized (SyncManager.class) {
            if (sInstance == null) {
                sInstance = new SyncManager();
            }
        }
        return sInstance;
    }

    private FirebaseJobDispatcher mDispatcher;

    private SyncManager() {
        Driver driver = new GooglePlayDriver(App.getInstance().getApplicationContext());
        mDispatcher = new FirebaseJobDispatcher(driver);
        Log.d(TAG, "Launched SyncManager: mDispatcher = " + mDispatcher.toString());
    }

    public void updateFeedSyncSchedule() {
        //TODO: get periodicity from Settings
        Log.d(TAG, "Scheduling feed sync ...");
        runFeedSyncJob((int) TimeUnit.MINUTES.toSeconds(1));
        //TODO: DELETE AFTER TESTING:
        CountDownTimer timer = new CountDownTimer(TimeUnit.MINUTES.toMillis(30), TimeUnit.MINUTES.toMillis(30)) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Stopping recurring sync");
                mDispatcher.cancel(TAG_FEED_SYNC);
            }
        };
        timer.start();
    }

    //TODO separate manual and scheduled syncs
    public void syncFeed() {
        Log.d(TAG, "Starting feed sync...");
        runFeedSyncJob(0);
        updateFeedSyncSchedule();
    }

    private void runFeedSyncJob(final int jobStartOffset) {
        boolean recurring = jobStartOffset > 0;
        Job job = mDispatcher.newJobBuilder()
                .setService(PodcastsSyncService.class)
                .setTag(TAG_FEED_SYNC)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK)
                .setTrigger(!recurring ? Trigger.NOW
                        : Trigger.executionWindow(jobStartOffset, jobStartOffset + 1))
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(recurring)
                .setReplaceCurrent(true)
                .build();

        int result = mDispatcher.schedule(job);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            PodcastsSyncEvent syncEvent = new PodcastsSyncEvent(PodcastsSyncEvent.Status.FAILED);
            syncEvent.setError("FirebaseJobDispatcher error code " + result);
            EventManager.getInstance().postEvent(syncEvent);
        }
    }
}
