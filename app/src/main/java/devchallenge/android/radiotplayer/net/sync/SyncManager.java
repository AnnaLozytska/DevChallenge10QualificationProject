package devchallenge.android.radiotplayer.net.sync;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import devchallenge.android.radiotplayer.App;

public class SyncManager {
    private static volatile SyncManager sInstance;
    private static final String TAG_FEED_SYNC = "feed_sync_tag";

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
    }

    public void updateFeedSync() {
        //TODO: get periodicity from Settings
        runFeedSyncJob((int) TimeUnit.HOURS.toSeconds(24), (int) TimeUnit.HOURS.toSeconds(1));
    }

    public void syncFeed() {
        runFeedSyncJob(0, 0);
    }

    private void runFeedSyncJob(final int periodicity, final int toleranceInterval) {
        boolean recurring = periodicity > 0;
        Job job = mDispatcher.newJobBuilder()
                .setService(FeedSyncService.class)
                .setTag(TAG_FEED_SYNC)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK)
                .setTrigger(!recurring ? Trigger.NOW
                        : Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setRecurring(recurring)
                .setReplaceCurrent(true)
                .build();

        int result = mDispatcher.schedule(job);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            // handle error
        }
    }
}
