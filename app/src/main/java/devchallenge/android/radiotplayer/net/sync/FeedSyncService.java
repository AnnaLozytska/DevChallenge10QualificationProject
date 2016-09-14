package devchallenge.android.radiotplayer.net.sync;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import devchallenge.android.radiotplayer.event.EventManager;
import devchallenge.android.radiotplayer.event.FeedSyncEvent;

import static devchallenge.android.radiotplayer.event.FeedSyncEvent.Status;

public class FeedSyncService extends JobService {
    private static final String TAG = FeedSyncService.class.getSimpleName();

    private EventManager mEventManager;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if (mEventManager == null) {
            mEventManager = EventManager.getInstance();
        }
        mEventManager.postEvent(new FeedSyncEvent(Status.STARTED));
        Log.d(TAG, "Run Feed sync");
        return false; /* if work is still being done*/
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mEventManager.postEvent(new FeedSyncEvent(Status.CANCELLED));
        return false; // true or false depending on if we need to reschedule
    }
}
