package devchallenge.android.radiotplayer.net.sync;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.squareup.otto.Produce;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.FeedSyncEvent;

import static devchallenge.android.radiotplayer.event.FeedSyncEvent.Status;

public class FeedSyncService extends JobService {
    private static final String TAG = FeedSyncService.class.getSimpleName();

    private EventManager mEventManager;
    private Status mCurrentStatus = Status.UNKNOWN;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mCurrentStatus = Status.STARTED;
        postSyncUpdate();
        Log.d(TAG, "Run Feed sync");
        return false; /* if work is still being done*/
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mCurrentStatus = Status.CANCELLED;
        postSyncUpdate();
        return false; // true or false depending on if we need to reschedule
    }

    @Produce
    private void postSyncUpdate() {
        if (mEventManager == null) {
            mEventManager = EventManager.getInstance();
        }
        mEventManager.postEvent(new FeedSyncEvent(mCurrentStatus));
    }
}
